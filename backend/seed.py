import os
import sys

# Ensure the parent directory is in the sys.path so we can import backend as a module
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from backend.database import SessionLocal, engine
from backend.models import Base, FoodItem, Beneficiary, DonationLog

def seed_data():
    # Create tables if they don't exist
    Base.metadata.create_all(bind=engine)

    db = SessionLocal()

    try:
        # Clear existing data so running seed.py multiple times is safe
        db.query(FoodItem).delete()
        db.query(Beneficiary).delete()
        db.query(DonationLog).delete()
        db.commit()

        # Seed Inventory
        inventory = [
            FoodItem(name="Milk", quantity=18, days_to_expiry=2),
            FoodItem(name="Beans", quantity=40, days_to_expiry=5),
            FoodItem(name="Rice", quantity=25, days_to_expiry=18),
            FoodItem(name="Wheat", quantity=12, days_to_expiry=30),
        ]
        db.add_all(inventory)

        # Seed Beneficiaries
        beneficiaries = [
            Beneficiary(name="Aisha", status="waiting"),
            Beneficiary(name="Ravi", status="waiting"),
            Beneficiary(name="John", status="waiting"),
        ]
        db.add_all(beneficiaries)

        # Seed Donations
        # Note: DonationLog schema requires item_name and quantity
        donations = [
            DonationLog(item_name="Rice donation", quantity=15),
            DonationLog(item_name="Canned beans", quantity=40),
            DonationLog(item_name="Milk donation", quantity=18),
        ]
        db.add_all(donations)

        db.commit()
        print("Database seeded successfully with demo data!")
    except Exception as e:
        db.rollback()
        print(f"Error seeding database: {e}")
    finally:
        db.close()

if __name__ == "__main__":
    seed_data()
