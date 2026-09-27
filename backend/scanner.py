import json
from google import genai
from google.genai import types

def analyze_food_package(image_bytes: bytes, mime_type: str) -> dict:
    """
    Analyzes an image of food packaging using the Gemini API.
    Returns a dictionary with 'name' and 'days_to_expiry'.
    """
    try:
        # Initialize the GenAI client. It will automatically look for the GEMINI_API_KEY environment variable.
        client = genai.Client()
        
        prompt = 'Analyze this food package and extract the item name and estimated days to expiry. Return ONLY a JSON object with keys name and days_to_expiry.'
        
        response = client.models.generate_content(
            model='gemini-2.5-flash',
            contents=[
                types.Part.from_bytes(data=image_bytes, mime_type=mime_type),
                prompt
            ],
        )
        
        result_text = response.text.strip()
        
        # Clean up Markdown JSON blocks if the model wrapped the response
        if result_text.startswith("```json"):
            result_text = result_text[7:]
        elif result_text.startswith("```"):
            result_text = result_text[3:]
            
        if result_text.endswith("```"):
            result_text = result_text[:-3]
            
        result_text = result_text.strip()
        
        data = json.loads(result_text)
        
        # Validate keys
        if "name" not in data or "days_to_expiry" not in data:
            raise ValueError("Missing required keys in AI response.")
            
        return data
        
    except json.JSONDecodeError:
        raise ValueError("Could not extract a valid JSON response from the image analysis.")
    except Exception as e:
        raise ValueError(f"Failed to process image or unreadable format: {str(e)}")
