import firebase_admin
from firebase_admin import credentials
from firebase_admin import db
from datetime import datetime
import base64
import boto3
import sys
import os
import picamera
import time
import asyncio
import paho.mqtt.client as mqtt


#파이어베이스 인증
cred=credentials.Certificate("final.json")
firebase_admin.initialize_app(cred,{
    'databaseURL' : '파이어베이스 주소'
})


#브로커 변수 및 시간날짜 ,사진경로
filepath="home/inhatc/final/test1.jpg"
broker_address="localhost"
broker_port=1883
topic="AMK/final"
now=""
now_hms=""
now_ymd=""
now_str=""

#s3 연결부
def s3_connection():
    try:
        s3=boto3.client(
            service_name="s3",
            region_name="ap-northeast-2",
            aws_access_key_id="액세스 키,
            aws_secret_access_key="시크릿키",
        )
        print("s3 연결성공")
        return s3
    except Exception as e:
        print(e)
        return False



#업로드 아마존s3 공식 개발자 문서 참고
def upload_file(s3,file_name, bucket, object_name=None):
    """Upload a file to an S3 bucket

    :param file_name: File to upload
    :param bucket: Bucket to upload to
    :param object_name: S3 object name. If not specified then file_name is used
    :return: True if file was uploaded, else False
    """

    # If S3 object_name was not specified, use file_name
    if object_name is None:
        object_name = os.path.basename(file_name)

    # Upload the file
    try:
        response = s3.upload_file(file_name, bucket, object_name)
    except Exception as e:
        print(e)
        return False
    return True


#파일명으로 url을 반환
def s3_get_url(s3, filename):
    """
    s3 : 연결된 s3 객체(boto3 client)
    filename : s3에 저장된 파일 명
    """
    return f"https://inhatcv.s3.ap-northeast-2.amazonaws.com/{filename}"


#카메라 영역
def camera():
    if os.path.isfile(filepath): #덮어쓰기가되나 혹시라도 중복시 삭제 후 다시만듦
        os.remove(filepath)
    camera=picamera.PiCamera()
    camera.capture('test1.jpg')
    time.sleep(3)
    camera.close()

#Subscribe 콜백함수
def on_message(client,userdata,msg):
    a=msg.payload.decode()
    maptt(a) #데이터받고 인수로 넘겨줌
def maptt(a):
    try:
        #try  catch로 완전히 commit 또는 rollback 
        #트랜잭션 atomy
        #매 콜백마다 날짜가 변경되므로 global선언
        global now
        global now_hms
        global now_ymd
        global now_str
        now=datetime.now()
        now_hms=now.strftime("%H:%M:%S")
        now_ymd=now.strftime("%Y-%m-%d")
        now_str=now.strftime("%Y%m%d%H%M%S")
        
        
        #파이어베이스 카메라 호출
        camera()
        dr=db.reference(now_ymd)
        
    
        
        #s3 연동코드
        img_url=""
        s3=s3_connection()
        print("연결유무",s3)
        if s3 != False:#연결이 정상이면
            filename="test1.jpg"
            fullfilename="/home/inhatc/final/"+filename 
            savename=now_str+".jpg"
            print("저장된이름",savename) 
            check=upload_file(s3,fullfilename,"inhatcv",savename)
            img_url=s3_get_url(s3,savename) #파일명을 날짜로 저장
        dr.update({now_hms:[a,"false","phone",img_url]})
        #파이어베이스 업데이트
    except Exception as e:
        print(e)

if __name__=="__main__":
    try:#mqtt 객체 선언 후 연결 
        client=mqtt.Client()
        client.on_message=on_message
        client.connect(host=broker_address, port=broker_port)
        client.subscribe(topic)
        client.loop_forever()
    except Exception as e:
        print(e)
