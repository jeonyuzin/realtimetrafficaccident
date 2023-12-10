# realtimetrafficaccident


<img src="https://github.com/jeonyuzin/realtimetrafficaccident/blob/main/readimg/pre.png">

자동차 사고가 났을 시 자동으로 알려주는 시스템을 제작한다.

IoT영역에서 초기 사고 감지를 한 후 연동된 시스템에 데이터를 보내고(위치와 현장 사진) 

스마트폰으로 위치와 현장 데이터(사진)을 확인하여 골든타임을 확보하고 상황실에서 경찰/소방관의 업무 피로도를 낮추는걸 목적으로 한다.


#사용 기술

클라우드
-AWS S3
-AWS EC2
-파이어 베이스

지도
-네이버 맵API

프로토콜
-MQTT(기기간 데이터 통신)


#시스템 구조도

<img src="https://github.com/jeonyuzin/realtimetrafficaccident/blob/main/readimg/structure.png">

#스마트폰(사용자 : 소방관,경찰분들이 현장을 파악 하고 처리하는 영역)
<img src="https://github.com/jeonyuzin/realtimetrafficaccident/blob/main/readimg/position.png">

#IoT영역
<img src="https://github.com/jeonyuzin/realtimetrafficaccident/blob/main/readimg/iot.png">
