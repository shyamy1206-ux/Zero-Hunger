from fastapi import FastAPI, Depends, HTTPException, File, UploadFile
from fastapi.middleware.cors import CORSMiddleware
from sqlalchemy.orm import Session
from pydantic import BaseModel
from typing import List, Optional

from . import models, database, scanner

# Create tables if they don't exist
models.Base.metadata.create_all(bind=database.engine)

app = FastAPI()

# Add CORS middleware
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Pydantic models for request bodies
class BeneficiaryCreate(BaseModel):
    name: str

class DonationLogCreate(BaseModel):
    item_name: str
    quantity: float


# Array & Sorting: GET /inventory
@app.get("/inventory")
def get_inventory(db: Session = Depends(database.get_db)):
    """Retrieve all FoodItem records, sorting them so items with the lowest days_to_expiry appear first."""
    items = db.query(models.FoodItem).all()
    # Sort items based on days_to_expiry
    sorted_items = sorted(items, key=lambda x: x.days_to_expiry)
    return sorted_items

# Searching: GET /inventory/search
@app.get("/inventory/search")
def search_inventory(name: str, db: Session = Depends(database.get_db)):
    """Linear search algorithm to find an item by name."""
    items = db.query(models.FoodItem).all()
    
    # Linear search
    for item in items:
        if item.name.lower() == name.lower():
            return item
            
    raise HTTPException(status_code=404, detail="Item not found")

# Queue (FIFO): POST /beneficiary/enqueue
@app.post("/beneficiary/enqueue")
def enqueue_beneficiary(beneficiary: BeneficiaryCreate, db: Session = Depends(database.get_db)):
    """Add a beneficiary to the line (Queue)."""
    db_beneficiary = models.Beneficiary(name=beneficiary.name, status="waiting")
    db.add(db_beneficiary)
    db.commit()
    db.refresh(db_beneficiary)
    return db_beneficiary

# Queue (FIFO): POST /beneficiary/dequeue
@app.post("/beneficiary/dequeue")
def dequeue_beneficiary(db: Session = Depends(database.get_db)):
    """Remove the first person to arrive from the waiting queue."""
    # Find the oldest waiting beneficiary (FIFO)
    first_waiting = db.query(models.Beneficiary)\
                      .filter(models.Beneficiary.status == "waiting")\
                      .order_by(models.Beneficiary.id.asc())\
                      .first()
    
    if not first_waiting:
        raise HTTPException(status_code=400, detail="Queue is empty")
    
    first_waiting.status = "served"
    db.commit()
    db.refresh(first_waiting)
    return {"message": f"Served {first_waiting.name}", "beneficiary": first_waiting}

# Stack (LIFO): POST /donation/push
@app.post("/donation/push")
def push_donation(donation: DonationLogCreate, db: Session = Depends(database.get_db)):
    """Add a new donation to the log (Stack)."""
    db_donation = models.DonationLog(item_name=donation.item_name, quantity=donation.quantity)
    db.add(db_donation)
    db.commit()
    db.refresh(db_donation)
    return db_donation

# Stack (LIFO): POST /donation/pop
@app.post("/donation/pop")
def pop_donation(db: Session = Depends(database.get_db)):
    """Undo the most recent donation entry (Stack)."""
    # Find the most recently added donation (LIFO)
    most_recent = db.query(models.DonationLog).order_by(models.DonationLog.id.desc()).first()
    
    if not most_recent:
        raise HTTPException(status_code=400, detail="Stack is empty")
    
    db.delete(most_recent)
    db.commit()
    return {"message": f"Successfully popped (undid) donation: {most_recent.item_name}", "popped_item": most_recent}


# AI Scanner: POST /api/scan-food
@app.post("/api/scan-food")
async def scan_food(file: UploadFile = File(...)):
    """Process an uploaded image of food packaging to extract item name and expiry using Gemini AI."""
    if not file.content_type.startswith("image/"):
        raise HTTPException(status_code=400, detail="Uploaded file must be an image.")
        
    try:
        contents = await file.read()
        # Ensure we pass a valid mime type to the AI model
        mime_type = file.content_type if file.content_type else "image/jpeg"
        
        result = scanner.analyze_food_package(contents, mime_type)
        return result
        
    except ValueError as e:
        # Catch our custom ValueError for bad images or failed parsing
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail="Internal Server Error during processing.")

