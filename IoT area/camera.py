import picamera
import time

camera=picamera.PiCamera()

time.sleep(3)
camera.capture("test1.jpg")
