from fastapi import FastAPI, File, UploadFile
from fastapi.responses import JSONResponse
from PIL import Image
import numpy as np
import onnxruntime as ort
import io
import torchvision.transforms as T

app = FastAPI()
session = ort.InferenceSession("best.onnx", providers=["CPUExecutionProvider"])

transform = T.Compose([
    T.Resize((512, 512)),
    T.ToTensor()
])

@app.post("/predict")
async def predict(file: UploadFile = File(...)):
    image = Image.open(io.BytesIO(await file.read())).convert("RGB")
    img = transform(image).unsqueeze(0).numpy()

    outputs = session.run(None, {"images": img})[0]
    boxes = outputs[..., :4]  # x1, y1, x2, y2
    scores = outputs[..., 4]
    # classes = outputs[..., 5]

    # confidence threshold 예시
    results = []
    for box, score in zip(boxes[0], scores[0]):
        if score > 0.4:
            results.append({
                "box": list(map(float, box)),
                "score": float(score)
            })

    return JSONResponse(content={"results": results})
