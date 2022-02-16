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


