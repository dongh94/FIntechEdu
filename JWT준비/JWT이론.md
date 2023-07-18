참고하면 좋을 site https://velog.io/@nyong_i/Spring-Security-docs%EB%A1%9C-%EA%B3%B5%EB%B6%80%ED%95%98%EA%B8%B0-01



SESSION - 서버 메모리에 저장 - 로드벨런싱 문제 - DB저장? ㄴㄴ

TCP - 물 데 네 트 세 프 응

* 응 : 스타
* 프 : 압축, 암호화
* 세 : 인증
* 트 : TCP (ack 신뢰성)/ UDP 신뢰성 X / 
* 네  : IP
* 데 : 찾아
* 물 : Hi

CIA : 기밀성(탈취) / 무결성(변화) / 가용성(그대로 사용) 

	1. A B C , A -> C 열쇠가 잘 전달되는지 문제
	1. 문서 - 누구로부터 왔는지

RSA : 암호화 ( 공개키 , 개인키 서로 풀수 있음 ) 공개키로 잠구면 -> 암호화 / 개인키로 잠구면 -> 전자서명

* 열쇠전달 문제 해결 : B 공개키 암호화 -> B 개인키 -> B만 열 수 있음
* 누구로 부터 왔는지 : A 개인키 암호화 -> A 공개키 -> 보낸이가 A인지 확인가능
* 즉, B 공개키로 잠구고 A 개인키로 잠군다.

jwt.io

* RFC란? - 약속된 규칙이 있는 문서다.
  * 약속된 규칙 - 프로토콜 - HTTP
  * RFC 1번 문서 -> 연결 -> 많아져 -> WWW

HTTP

* header - HS256
* payload - { object : 1 }
* signiture - header, payload -> HS256
  * **H**MAC -> 시크릿 키를 포함한 암호화 방식
  * **S**HA256 -> 해쉬 암호
* 위 3개를 Base64인코딩해서 JWT를 만들어서
* 클라이언트의 localstorage 에 저장
* 클라이언트는 서버에 JWT들고 요청 - **1**
* 서버는 header, payload + 서버 개인키로 암호화 해서 동일한지 비교(검증) 후 응답 - **2**
* 클라이언트는 서버 공개키로 해결 - **3**



세션

* 서버가 많을 때 (로드벨런서) 안좋아.

쿠키

* 클라이언트에 저장

* 서버는 인증할 때 http only -> 밑에 방식이 안된다. 왜?

  * ```javascript
    fetch("http://www.naver.com", {
        headers : {
            Cookie: "..."
        }
    })
    ```

  * 서버에서 이 방식을 풀어주면 javascript로 장난질 가능하다.

http-basic

* headers -> Anthorization : ID, PW
* 요청할 때마다 달고 간다. ID, PW 장난질 가능
* 그래서 Anthorization 에 토큰 사용한다. -> Bearer 방식 (유효시간) -> JWT 사용



Security : login이 이미 등록되어 있다.



AuthenticationManager로 로그인 시도를 하면, PrincipalDetailsService가 호출

loadUserByUsername() 이 실행되는 것.

PrincipalDetails를 세션에 담고 (권한 관리를 위해서)

JWT 토큰을 만들어서 응답해주면 된다.



request.getInputStream() 확인방법

```java
try {
	BufferedReader br = request.getReader();
	
	String input = null;
	while ((input = br.readline()) != null) {
		System.out.println(input);
	}
} catch (IOException e){
    e.printStackTrace();
}
```



권한과 시간 연장

* authentication 객체가 PrincipalDetails를 session 영역에 저장하고 그 객체 return 해주면 됨.

* 리턴의 이유는 권한 관리를 security가 대신해주기 때문에 편하려고 하는 거고 (시간연장)

* 굳이 JWT 토큰을 사용하면서 세션을 만들 이유가 없음. 권한 처리 때문에 session 넣어 주는 것.

* 권한 : role : user, admin, manager 등





​	