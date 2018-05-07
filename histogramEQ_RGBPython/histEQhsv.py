import cv2
import numpy as np

image = cv2.imread('poppy.jpg')
hsv = cv2.cvtColor(image, cv2.COLOR_RGB2HSV)

v = hsv[:,:,2]

cv2.imwrite('v.jpg', v)

v_equ = cv2.equalizeHist(v)

hsv[:,:,2] = v_equ

equ = cv2.cvtColor(hsv, cv2.COLOR_HSV2RGB)

cv2.imwrite('equ.jpg', equ)
