ExOf는 Socket을 담당하는 Listener, 비즈니스 로직을 담당하는 Service, 주기적으로 Service를 호출할 수 있는 Scheduler 로 구성되어 있다.

## Listener
Netty를 사용하며 있으며 크게 3 부분으로 나눠진다.
* [Session Event Handler](https://github.com/KNero/ExOf/wiki/Listener_SessionHandler)
* [Socket Data Handler](https://github.com/KNero/ExOf/wiki/Listener_ChannelHandler)
* [Socket Data transform](https://github.com/KNero/ExOf/wiki/Listener_Transform)

 Listener은 [ChannelHandler](https://github.com/KNero/ExOf/wiki/Listener_ChannelHandler) 와 [MessageTransform](https://github.com/KNero/ExOf/wiki/Listener_Transform) 이 필수적으로 설정되어야 한다.

## [Service](https://github.com/KNero/ExOf/wiki/Service)
비즈니스 로직을 담당하며 서비스 전 처리를 위한 Inbound, 서비스 후 처리를 위한 Outbound를 연결할 수 있다.

## [Scheduler](https://github.com/KNero/ExOf/wiki/Scheduler로_Service_호출)
Quartz를 사용하며 service.xml 에서 간단한 설정을 통해서 원하는 Service를 주기적으로 실행시켜 준다.

## [Console](https://github.com/KNero/ExOf/wiki/추가기능#콘솔-모니터링)
command를 사용한 서버 정보 검색 및 설정
