# Cryptonite
- 2016 2-1 객체지향프로그래밍 수업 프로젝트
- 자바로 구현한 Endpoint 보안 솔루션입니다. 보호하고자 하는 파일들을 AES-256으로 암호화하여 안전하게 보관합니다. 
- 암호화 키론 PBKDF2로 해싱된 패스워드를 사용하여 서버에 암호화키를 저장하지 않도록 했습니다. 
- 가용성을 보장하기 위해 서버백업기능을 지원하여 손상되었을 때 언제든 다시 복구할 수 있습니다. 
- 사용자간에 암호화된 파일을 공유할 수 있습니다.

## 기능
- 파일 자동 암호화
 - 사용자의 보호폴더의 변화 확인시 실시간 폴더감지 모듈에서 감지.
 - 암호화 알고리즘 AES-256을 사용하여 대상파일을 암호화.
 - 암호화 키는 PBKDF2로 해싱된 패스워드.
- 파일 자동 백업
 - 파일이 암호화되면 실시간 폴더감지 모듈에서 즉시 감지하고 서버로 백업.
- 파일 복원
- 유저간의 파일 전송수신
 - 1:1 파일 공유로써 보내고자하는 사람이 전송하고자 하는 파일을 선택하면 OTP가 발급. 
 - 그 OTP를 파일을 받는 사람이 입력하면 파일을 받을 수 있음.
 - 한번 받은 파일은 서버에서 삭제되며 서버에서의 최대 보관기한은 48시간.
- 위치 추적 기능
 - whois api를 활용하여 ISP 단위 위치 확인. 도난을 방지.
 
 ## 스택
 - Java
 - Swing
 - Java Crypto Lib
