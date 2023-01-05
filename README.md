
### 개요
2022년 2학기 중앙대학교 융합 iot 수업에서 팀 프로젝트로 만든 프로젝트입니다. 

건물 내부에선 gps의 정확도가 매우 낮다는 문제점에 착안하여,
건물 내부에서 사용자의 위치를 파악하고 이를 매핑해서 사용자에게 알려주는 시스템입니다.

### 사용한 기술
- 블루투스 rssi
- imu dead-reckoning
- 삼각측량
- particle filter

### 결과물
main 브랜치
- android
  - 설정한 비콘 2개에 대해 블루투스 rssi 값 측정
  - imu dead-reckoning
- final_server 
  - android에서 전송된 2개의 rssi값에 대해 삼각측량, particle filter 적용해 위치 특정
  
