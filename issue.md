### PasswordEncoder 구현체
> 기존의 password hashing 전용 알고리즘인 bcrypt나 scrypt 는 컴퓨팅 파워가 지금보다 딸리고 GPU 나 전용 ASIC 으로 병렬 계산이 어려운 시대에 설계되었으므로 새로운 알고리즘이 필요하다.

- Argon2PasswordEncoder
  1. Argon2 해시 함수 사용 (password hashing contest 2015에서 우승)
      - Argon2d
          > 더 빠르고 data-depending 메모리 액세스를 사용하므로 비밀 해싱에 덜 적합하고 부 채널 타이밍 공격의 위협이 없는 암호 화폐 및 애플리케이션에 더 적합하다.
      - Argon2i
          > 암호 해싱 및 암호 기반 키 유도에 적합한 데이터 독립적 메모리 액세스를 사용한다. Argon2i는 트레이드 오프 공격으로부터 보호하기 위해 메모리를 더 많이 통과하므로 속도가 느리다.
      - Argon2id
          > argon2i 와 argon2d 와 하이브리드로 data-depending 과 data-independent memory access 를 혼용할 수 있으며 password hashing 과 password 기반 키 유도에 적합하다.
  3. Memory-hard function 사용
  4. argon2 는 암호를 해싱하는데 걸리는 시간이나 소요되는 메모리 양을 설정할 수 있게 설계되었으므로 사용하는 목적에 맞게 파라미터 변경으로 적용이 가능

- BCryptPasswordEncoder
  1. BCrypt 해시 함수 사용 [BCrypt](https://velog.io/@sangmin7648/Bcrypt%EB%9E%80)
      > BCrypt는 단방향 암호화지만, **Blowfish**를 이용해서 구현했다. 최신 GPU를 이용해, SHA-256을 1초에 굉장히 많이 수행할 수 있다. 
      >  때문에 Rainbow Table 공격에 취약하다. 이를 해결하기 위해 **Blowfish**가 등장했다. hash function **반복 횟수**를 변수로 지정가능하게 하여, **연산속도를 늦춤**으로써 
      >  **brute force에 대비**할 수 있다.
      >> **Rainbow Table**이란? 
      >> 
      >> 많이 쓰여지는 비밀번호의 hash value를 미리 만들어놓고, 유출된 has value와 비교하여 원본 비밀번호를 찾아내는 방법
 
 [참고자료](https://velog.io/@kylexid/%EC%99%9C-bcrypt-%EC%95%94%ED%98%B8%ED%99%94-%EB%B0%A9%EC%8B%9D%EC%9D%B4-%EC%B6%94%EC%B2%9C%EB%90%98%EC%96%B4%EC%A7%88%EA%B9%8C)
 
 [참고자료](https://www.lesstif.com/security/argon2-password-hashing-95879198.html)
 
 [핵심내용](https://d2.naver.com/helloworld/318732)

-------
### @RequestParam vs @PathVariable

1. **@RequestParam**은 query parameters를 추출하기 위해 사용되는 반면, **@PathVariable**은 URI로부터 data를 추출하기 위해 사용된다.

2. **@RequestParam**은 query parameter들이 쓰인 traditional Web application에 더 적합하다. 반면에, **@PathVariable**은 ***RESTful API Server***에 더 적합하다 (e.g. http://localhost:8080/book/9783827319333)

-------

###MariaDB를 선택한 이유
1. 가격
    - 상용 데이터베이스인 오라클, MSSQL이 MariaDB, MySQL, PostgreSQL보다 동일 사양 대비 AWS 가격이 더 높다.
    
2. Amazon Aurora 교체 용이성
    - Amazon Aurora는 AWS에서 MySQL과 PostgreSQL을 클라우드 기반에 맞게 재구성한 데이터베이스이다.
    - 공식 자료에 의하면 RDS MySQL 대비 5배, RDS PostgreSQL보다 3배의 성능을 제공한다.그러나, 최소 비용이 월 최소 10만원 이상이기 때문에 MariaDB를 선택. 
   
3. MySQL vs MariaDB
    - MySQL의 창시자인 몬티 와이드니어가 만든 프로젝트가 MariaDB
    - 동일 하드웨어 사양으로 MySQL보다 향상된 기능
    - [MySQL에서 MariaDB로 마이그레이션 해야 할 10가지 이유](https://xdhyix.wordpress.com/2016/03/24/mysql-에서-mariadb-로-마이그레이션-해야할-10가지-이유/)

--------
### HTTP Cache

Web Browser( + Browser Cache) <-> Web Server

> Browser Cache는 **max-age, Etag, data**를 가지고 있다.

> **Etag** : Server에서 Content를 이용해 만든 Hash Value
```
1. Web Browser가 Web Server에게 Request 요청을 한다.

2. if (max-age가 만료되지 않았다면) -> Browser Cache에 저장된 데이터를 Response 받는다.

   else if (max-age가 만료됐다면) -> Server에게 Etag와 함께 data를 Request한다.
                   if (Server는 Browser가 보낸 Etag와 자신이 가지고 있는 요청 리소스 Etag가 같다고 판단하면) 
                            -> 304 NotModified + Etag + max-age Response한다.
                   else if (Etag가 다르다고 판단한다면) -> 새로운 data + Etag + max-age를 response한다.
```

:question: 만약 max-age가 만료되지 않았을 때, Web Server에 새로운 소스가 배포된 상황이라면?
> max-age가 만료되지 않았기 때문에 Web Browser는 ***Browser Cache에 저장된 이전 데이터***를 받아올 것이다. 
> 
> 이런 문제점을 해결하기 위해, 새로운 소스 배포 시 리소스명에 version명을 붙여 배포한다. 이렇게 하면 Web Browser는 새로운 리소스 요청으로 인식하여 새롭게 배포된 소스를 받아오게 된다.

--------
### 좋아요 기능에서의 동시성 이슈
:bulb: **좋아요 버튼을 다수의 사용자가 동시에 눌렀을 때 동시성 이슈가 발생할 수 있다**

  > JPA는 객체 조회 시, 영속성 컨텍스트의 1차 캐시에서 데이터를 조회하기 때문에 '일관성 없는 읽기'의 문제가 존재하지 않는다.
  >
  > 따라서, 해결해야하는 문제는 'Lost Update'이다.

:pill: 해당 문제를 해결하기 위해서 2가지 방법이 존재한다.

1. Optimistick Lock (낙관적 잠금)
    - version 컬럼을 이용해 DB data를 update 한다.
      > A Transaction, B Transaction이 동시에 select로 특정 row를 조회
      
      > A Transaction이 해당 row의 특정 column을 update -> version++
      
      > B Transaction은 조회했을 때의 version과 A Transaction 이후의 version이 다르게 때문에, update 할 수 없게 됨
  
    - @Version을 이용해 구현 가능
  
    - Read는 가능, But Write 시점에 충돌 감지 -> 때문에 Transaction Commit까지는 충돌 여부를 알 수 없다.
    - 충돌 시, ObjectOptimisticLockingFailureException 예외 발생. 개발자가 직접 Rollback을 구현해야 한다.
    - 활동성은 좋으나, Transaction Commit까지는 충돌 여부를 알 수 없다는 단점이 존재
    - Transaction 충돌이 거의 발생하지 않고, Read가 많은 곳에 사용하는 것이 적절하다.
  
2. Pessimistic Lock (비관적 잠금)
    - DB의 Lock을 이용해 Entity를 영속상태로 올릴 때부터 다른 Session에서 조회하지 못하도록 잠근다.
    - 충돌 시, Transaction 성질에 따라 Rollback이 자동으로 일어난다.
    - 활동성은 낮으나, 정확성이 보장된다.
    - 트랜잭션 충돌이 빈번한 곳에 사용하는 것이 적절하다.

