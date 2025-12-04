# ml_model.py
from ultralytics import YOLO

# Charger ton modèle
model = YOLO("C:/Users/rayen/runs/classify/train4/weights/best.pt")
# #model = YOLO("C:/Users/rayen/OneDrive/Bureau/l3-dsi1/projerintegration/rod_balek.v6i.folder/runs/train/car_model_v12/weights/best.pt")