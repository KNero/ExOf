# ExOf 구성요소

## Listener
Socket 으로 들어온 요청을 Service 가 처리할 수 있는 형태로 변형하는 역할을 한다.
* [Session Event Handler](https://github.com/KNero/ExOf/wiki/Listener_SessionHandler)
* [Socket Data Handler](https://github.com/KNero/ExOf/wiki/Listener_ChannelHandler)
* [Socket Data transform](https://github.com/KNero/ExOf/wiki/Listener_Transform)

    Listener은 [ChannelHandler](https://github.com/KNero/ExOf/wiki/Listener_ChannelHandler) 와 [MessageTransform](https://github.com/KNero/ExOf/wiki/Listener_Transform) 이 필수적으로 설정되어야 한다.

## [Service](https://github.com/KNero/ExOf/wiki/Service)
비즈니스 로직을 담당하며 서비스 전 처리를 위한 Inbound, 서비스 후 처리를 위한 Outbound를 연결할 수 있다.

## [Scheduler](https://github.com/KNero/ExOf/wiki/Scheduler로_Service_호출)
service.xml 에서 간단한 설정을 통해서 원하는 Service를 주기적으로 실행시켜 준다.

## [Console](https://github.com/KNero/ExOf/wiki/Console_monitoring)
CLI를 사용한 서버 정보 검색 및 설정

## [Wiki](https://github.com/KNero/ExOf/wiki)

## [Release](https://github.com/KNero/ExOf/releases)
