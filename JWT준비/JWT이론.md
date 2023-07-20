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



---

# #1



## Spring Security

`스프링 시큐리티`를 처음 접하는 사람들이 정확하게 이해해서 `인증`, `인가` 처리를 구현하는 것은 쉽지 않다.

나 또한 스프링 시큐리티의 진입 장벽이 낮지 않다고 생각하고 단순히 구글링해서 클론코딩으로만 `인증`, `인가`를 구현하는 것은 썩 좋지 않다고 생각한다.

때문에 이번 기회에 [Spring Security docs](https://docs.spring.io/spring-security/reference/servlet/getting-started.html)를 읽어보면서 스프링 시큐리티의 `동작원리`, `인증`, `인가` 등등 자세히 알아보자.

------

## Updating Dependencies

스프링 시큐리티를 사용하려면 `Maven` 또는 `Gradle`을 이용하여 종속성을 업데이트 하면 된다고 한다.

### Maven

```xml
<dependencies>
	<!-- ... other dependency elements ... -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-security</artifactId>
	</dependency>
</dependencies>
```

### Gradle

```java
dependencies {
	compile "org.springframework.boot:spring-boot-starter-security"
}
```

의존성을 추가한 뒤 서버를 실행시키면 아래와 같은 화면이 나타난다.
![img](https://velog.velcdn.com/images/nyong_i/post/c27d2f89-3a6b-4243-b266-029d74293949/image.png)

------

## Spring Boot Auto Configuration

`Spring Boot`는 자동으로 `Spring Security`의 기본 구성(서블릿 `Filter`와 `springSecurityFilterChain`)을 활성화한다.

`Filter`와 `springSecurityFilterChain`은 `빈`이며, 이는 응용 프로그램 내에 모든 보안(`응용 프로그램 URL 보호`, `사용자 이름 및 비밀번호 확인`, `로그인 양식으로 리디렉션` 등)을 담당한다고 한다.

Spring Boot는 많은 설정을 하지는 않지만 많은 작업을 수행할 수 있다.(Spring Security 덕분에)

#### 기능요약은 다음과 같다.

- 응용 프로그램과의 모든 상호 작용을 위해 인증된 사용자 필요
- 기본 로그인 양식 생성
- 사용자 이름 `user`와 콘솔에서 제공하는 비밀번호를 사용하여 인증
- `BCrypt`를 사용해서 비밀번호를 `암호화`
- 로그아웃
- `CSRF` 공격 방지
- 세션 고정 보호
- 보안 헤더 통합
  - 보안 요청을 위한 HTTP Strict Transport Security
  - X-Content-type-Options 통합
  - 캐시 제어(나중에 애플리케이션에서 재정의하여 정적 리소스를 캐싱할 수 있음)
  - X-XSS-Protection 통합
  - 클릭재킹 방지를 위한 X-Frame-Options 통합

---



# #2



이번 섹션에서는 `Servlet` 기반 애플리케이션 내에서 `Spring Security`의 `고수준 아키텍쳐`에 대해서 설명한다.

`참조의 인증`, `권한 부여`, `악용에 대한 보호`를 사용할 때 아키텍쳐에 대한 높은 수준의 이해를 요구한다고 한다.

------

## A Review of Filters

`Spring Security`의 `Servlet` 지원은 `Servlet`을 기반으로 하므로 `Filter`의 역할을 먼저 살펴보는 것이 도움이 된다.

아래의 `Filter` 그림은 단일 `HTTP` 요청에 대한 처리기의 일반적인 계층화를 보여준다.

![img](https://velog.velcdn.com/images/nyong_i/post/e523b85b-4340-4aa1-96f7-114c48009020/image.png)

클라이언트는 애플리케이션에 요청을 보내고 컨테이너는 `FilterChain`를 포함하고 `요청 URI`의 경로를 기반으로 처리한다.

------

## DelegatingFilterProxy

`Spring`은 Servlet `Filter`와 `DelegatingFilterProxy`컨테이너의 라이프사이클과 Spring의 `ApplicationContext`를 지원한다.

`DelegatingFilterProxy`는 `서블릿 컨테이너`와 `스프링 컨테이너(어플리케이션 컨텍스트)` 사이의 링크를 제공하는 `ServletFilter`이다.
특정한 이름을 가진 스프링 빈을 찾아 그 빈에게 요청을 `위임`한다.

**Why?** `서블릿 필터(서블릿 컨테이너)`와 `시큐리티 필터(스프링 컨테이너)`는 서로 다른 컨테이너에서 생성되고 동작하기 때문에 이를 연결해줄 것이 필요함

다음은 `DelegatingFilterProxy`의 동작 방식이다.
![img](https://velog.velcdn.com/images/nyong_i/post/96443098-835c-4520-b462-d4d99ed1c56f/image.png)

`DelegatingFilterProxy`에서 `Bean Filter`를 찾은 다음 `ApplicationContext`를 호출한다.

무슨말인지 잘 이해가 안될 수 있지만 아래 그림을 보면 이해가 좀 더 쉬울 것이다.

![img](https://velog.velcdn.com/images/nyong_i/post/aff22d45-eacc-4750-86a5-4d34f7ab8e61/image.png)

참고로 `DispatcherServlet`보다 `DelegatingFilterProxy`의 우선순위가 더 높다.

따라서 `DispatcherServlet`이 요청을 다 가로채갈일은 없다고 보면된다.

### DelegatingFilterProxy 의사 코드

```java
public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
	Filter delegate = getFilterBean(someBeanName);
	delegate.doFilter(request, response);
}
```

정리해서 `DelegatingFilterProxy`까지 적용된 요청의 동작 순서는 `Request`가 `Filter`(Servlet 컨테이너)를 거치다가 `DelegatingFilterProxy`를 만나면 `ApplicationContext`(Spring 컨테이너)로 이동해서 인증을 마저 진행한다.

사진으로 보면 다음과 같다.
![img](https://velog.velcdn.com/images/nyong_i/post/cfb7c011-7ac5-4ea3-a2e4-3034a84bc8b0/image.png)

------

## FilterChainProxy

`Spring Security`의 `Servlet` 지원은 `FilterChainProxy`를 통해 많은 인스턴스에 위임할 수 있는 특수한 기능도 포함되어 있다.

![img](https://velog.velcdn.com/images/nyong_i/post/87823c0f-292a-483f-9781-2219a4a9681a/image.png)

`Spring 컨테이너`로 도착한 요청은 `DelegatingFilterProxy`로부터 요청을 `위임` 받고 실제로 보안을 처리한다.

사용자의 요청을 필터 순서대로 호출하여 전달한다. 사용자 정의 필터를 생성해서 기존의 필터 전후로 추가 가능하다.

**마지막 필터**까지 `인증` 및 `인가` **예외가 발생하지 않으면** 보안을 통과한다.

------

## SecurityFilterChain과 Security Filters

![img](https://velog.velcdn.com/images/nyong_i/post/b25c7192-035c-474c-83d8-46309f36b6b1/image.png)

`FilterChainProxy`에서 잠깐 살펴 봤던것과 같이 **마지막 필터**까지 `인증` 및 `인가` **예외가 발생하지 않으면** 보안을 통과한다.

스프링 시큐리티에서 제공하는 필터는 다음과 같다.

- ForceEagerSessionCreationFilter
- 채널 처리 필터
- WebAsyncManagerIntegrationFilter
- SecurityContextPersistenceFilter
- 헤더라이터 필터
- CorsFilter
- CSRF 필터
- 로그아웃 필터
- OAuth2AuthorizationRequestRedirectFilter
- Saml2WebSso 인증 요청 필터
- X509 인증 필터
- AbstractPreAuthenticatedProcessingFilter
- CasAuthenticationFilter
- OAuth2Login 인증 필터
- Saml2WebSso 인증 필터
- UsernamePasswordAuthenticationFilter
- OpenID인증 필터
- 기본 로그인 페이지 생성 필터
- 기본 로그아웃 페이지 생성 필터
- 동시 세션 필터
- DigestAuthenticationFilter
- BearerTokenAuthenticationFilter
- BasicAuthenticationFilter
- 요청 캐시 인식 필터
- SecurityContextHolderAwareRequestFilter
- JaasApiIntegrationFilter
- RememberMeAuthenticationFilter
- 익명 인증 필터
- OAuth2AuthorizationCodeGrantFilter
- 세션 관리 필터
- ExceptionTranslationFilter
- FilterSecurityInterceptor
- SwitchUserFilter



# #3



![post-thumbnail](https://velog.velcdn.com/images/nyong_i/post/e28fac4c-06c8-421a-817e-7086a67ef3e5/image.png)

`Spring Security`는 `인증`에 대한 포괄적인 지원을 제공한다.

------

## Authentication Mechanisms

1. 사용자 이름 및 비밀번호 - 사용자 이름/비밀번호로 인증하는 방법
2. OAuth 2.0 로그인 - `OAuth 2.0 OpenID Connect` 및 `비표준 OAuth 2.0` 로그인 (ex: 네이버 로그인, 카카오 로그인, 깃허브 로그인 ..)
3. SAML 2.0 로그인 : SAML 2.0 로그인
4. 중앙 인증 서버(CAS) : 중앙 인증 서버 지원
5. RememberMe : 세션 만료 후 사용자 정보를 기억함
6. JAAS 인증 : `JAAS`로 인증
7. OpenID : `OpenID` 인증(`OpenID Connect`와 혼동 X)
8. 사전 인증 시나리오 : `SiteMinder` 또는 Java EE 보안과 같은 외부 매커니즘으로 인증을 하지만 일반적인 악용에 대한 권한 부여 및 보호를 위해 여전히 `Spring Security`를 사용
9. X509 인증 : X509 인증

간단하게 스프링 시큐리티 내에서 **폼 기반 로그인**이 어떻게 작동하는지 살펴보자.

------

## Form Login

Spring Security는 html 형식을 통해 제공되는 사용자 이름과 비밀번호에 대한 지원을 제공한다.

![img](https://velog.velcdn.com/images/nyong_i/post/4eaee4c8-8698-471a-a7d2-2ae56b5906ab/image.png)

### 인증되지 않은 상태(로그인 X)에서 권한이 필요한 리소스에 접근하려고 할 때

1. 먼저 사용자가 권한이 없는 리소스 `/private`에 대해 인증되지 않은 요청을 한다.
2. `Spring Security`는 인증 `FilterSecurityinterceptor`되지 않은 요청이 `AccessDeninedException`에 의해서 `ExceptionTranslationFilter`로 반환된다.
3. 사용자가 인증되지 않았으므로 로그인 페이지로 `리디렉션`한다.
4. 브라우저는 `리디렉션`된 로그인 페이지를 요청한다.
5. 애플리케이션 내에서 로그인 페이지를 `렌더링` 한다.

로그인 페이지가 렌더링 되고 사용자가 `사용자 이름`과 `암호`를 제출하면 `UsernamePasswordAuthenticationFilter`로 인증한다.
![img](https://velog.velcdn.com/images/nyong_i/post/a04b2b44-7c10-4590-bce9-aff4a50b008c/image.png)

### 로그인 과정

1. `사용자 이름`과 `암호`를 추출하여 `UsernamePasswordAuthenticationFilter`를 거친다.

2. `UsernamePasswordAuthenticationToken`으로 전달된다.

3. 인증에 실패하면

    

   ```
   Failure
   ```

   - `SecurityContextHolder`가 지워진다.
   - `RememberMeServices.loginFail`이 호출된다.
   - `AuthenticationFailureHandler`가 호출된다.

4. 인증에 성공하면

    

   ```
   Success
   ```

   - `SessionAuthenticationStrategy`에서 새로운 로그인 알림을 받음
   - `RememberMeServices.loginSuccess` 호출
   - `ApplicationEventPulbisher` 발행
   - `AuthenticationSuccessHandler` 호출 일반적으로 이것은 로그인 페이지로 리디렉션할 때 `SimpleUrlAuthenticationSuccessHandler` 저장된 요청으로 리디렉션

​	

# #4

## Authorities

`Authentication`, 모든 구현이 개체 `Authentication`목록을 저장하는 방법을 설명한다. `GrantedAuthority`는 본인에게 부여된 권한을 나타낸다.

개체는 `GrantedAuthority` 개체에 삽입되고 나중에 권한 부여 결정을 내릴때 둘 중 하나에 의해 읽힌다.

아래는 `GrantedAuthority` 하나의 메서드만 있는 인터페이스다.

```java
String getAuthority();
```

이 방법을 사용하면 `Authorizationmanager`의 정확한`String` 표현을 얻을 수 있다.

------

## Pre-Invocation Handling

`Spring Security`는 `메소드 호출` 또는 `웹 요청`과 같은 보안 객체에 대한 액세스를 제어하는 `인터셉터`를 제공한다.

### The AuthorizationManager

`AuthorizationManager` `AccessDecisionManager` 및 `AccessDecisionVoter`을 모두 대체한다.

`AccessDecisionManager`를 사용 하도록 변경 하거나 사용자 지정하는 응용 프로그램이다.

`AuthorizationManagers`는 `AuthorizationFilter`에 의해 호출되며 최종 액세스 제어 결정을 내릴 책임이 있다.

`AuthorizationManager` 인터페이스에는 두 가지 메서드가 있다.

```java
AuthorizationDecision check(Supplier<Authentication> authentication, Object secureObject);

default AuthorizationDecision verify(Supplier<Authentication> authentication, Object secureObject)
        throws AccessDeniedException {
}
```

------

## 위임 기반 AuthorizationManager 구현

사용자가 `권한 부여`의 모든 측면을 제어하기 위해 자신의 것을 구현할 수 있지만, `Spring Security`는 `Authorizationmanager`를 위임한다.

![img](https://velog.velcdn.com/images/nyong_i/post/be6d5d86-020a-4caf-9b60-321a1dd12245/image.png)

------

## 계층적 역할

`계층적 역할`이라는 것은 `특정 역할`이 `다른 역할`을 `포함`하는 것이다.

예를 들어서 `USER`, `MANAGER`, `ADMIN` 3개의 권한이 있다.
(권한은 오름차순으로 높다.)

따라서 `MANAGER`는 `USER`의 권한을 가지고 있어야 하고, `ADMIN`은 `USER`, `MANAGER` 2개의 권한을 모두 다 가지고 있어야 한다.

이러한 계층적 역할을 지원하는 `RoleVoter`은 `RoleHierarchyVoter`로 구성되어 `RoleHierarchy` 사용자에게 할당된 모든 `접근 가능한 권한`을 얻는다.

```java
@Bean
AccessDecisionVoter hierarchyVoter() {
    RoleHierarchy hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("ROLE_ADMIN > ROLE_STAFF\n" +
            "ROLE_STAFF > ROLE_USER\n" +
            "ROLE_USER > ROLE_GUEST");
    return new RoleHierarchyVoter(hierarchy);
}
```



# #5 코딩

![post-thumbnail](https://velog.velcdn.com/images/nyong_i/post/d5ef4b46-99f6-4946-bf30-95fdd324cd3a/image.png)

## 초기 설정

### build.gradle

```java
plugins {
	id 'org.springframework.boot' version '2.7.1'
	id 'io.spring.dependency-management' version '1.0.11.RELEASE'
	id 'java'
}

group = 'the.plural'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '11'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
	implementation 'org.springframework.boot:spring-boot-starter-security'
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'org.springframework.boot:spring-boot-starter-validation'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	implementation 'org.springframework.boot:spring-boot-starter-websocket'
	implementation 'org.thymeleaf.extras:thymeleaf-extras-springsecurity5'
	implementation 'io.jsonwebtoken:jjwt:0.9.1'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'mysql:mysql-connector-java'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	testImplementation 'org.springframework.security:spring-security-test'
}

tasks.named('test') {
	useJUnitPlatform()
}
```

------

데이터베이스는 `mysql`을 사용하고 `Jwt` 토큰 기반 방식으로 로그인할 것이기 때문에 `jwt` 관련 설정을 추가해준다.

### application.yml

```yml
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/데이터베이스 이름?serverTimezone=Asia/Seoul&characterEncoding=UTF-8
    username: 유저네임
    password: 비밀번호

  security:
    jwt:
      header: Authorization
      secret: c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK
      token-validity-in-seconds: 86400

  jpa:
    database: mysql
    database-platform: org.hibernate.dialect.MySQL5InnoDBDialect
    generate-ddl: true
    hibernate:
      ddl-auto: update
    show_sql: true
    format_sql: true

#    default_batch_fetch_size: 1000

logging.level:
  org.hibernate.SQL: debug
  org.hibernate.type: trace
  # parameter Binding
```

------

## 회원가입

### BaseTimeEntity

```java
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;
}
```

- createDate : 엔티티가 생성된 시간
- modifiedDate : 엔티티가 수정된 시간

엔티티의 생성시간과 수정시간 컬럼을 만들기 위해서 `BaseTimeEntity` 클래스를 생성한다.

------

### Application

```java
@EnableJpaAuditing // 이 부분
@SpringBootApplication
public class BerrApplication {

	public static void main(String[] args) {
		SpringApplication.run(BerrApplication.class, args);
	}

}
```

`BaseTimeEntity`를 만들었다면 꼭 `Application`에 `@EnableJpaAuditing`을 추가해줘야 한다.

------

### Member

```java
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Entity
public class Member extends BaseTimeEntity {

    @Id @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 45, unique = true)
    private String email;

    @Column(length = 45)
    private String nickname;

    private int age;

    @Column(length = 100)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
    
    public void encodePassword(PasswordEncoder passwordEncoder){
        this.password = passwordEncoder.encode(password);
    }
```

로그인할 `ID`는 `Email`이다.

------

### Role

```java
public enum Role {
    USER, MANAGER, ADMIN;
}
```

`enum` 클래스인 것에 주의해서 설계하자

------

### Repository

```java
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
```

`Extends`는 `JpaRepository<엔티티, 엔티티 PK 타입>`이다.
`findByEmail`은 JPA에서 지원하는 `쿼리 메소드`이며 이메일이 일치하는 `Member`를 찾아준다.

------

### MemberSignUpRequestDto

```java
@Data
@Builder
@AllArgsConstructor
public class MemberSignUpRequestDto {

    @NotBlank(message = "아이디를 입력해주세요")
    private String email;

    @NotBlank(message = "닉네임을 입력해주세요.")
    @Size(min=2, message = "닉네임이 너무 짧습니다.")
    private String nickname;

    @NotNull(message = "나이를 입력해주세요")
    @Range(min = 0, max = 150)
    private int age;

    @NotBlank(message = "비밀번호를 입력해주세요")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,30}$",
            message = "비밀번호는 8~30 자리이면서 1개 이상의 알파벳, 숫자, 특수문자를 포함해야합니다.")
    private String password;

    private String checkedPassword;

    private Role role;

    @Builder
    public Member toEntity(){
        return Member.builder()
                .email(email)
                .nickname(nickname)
                .age(age)
                .password(password)
                .role(Role.USER)
                .build();
    }
}
```

------

서비스 계층은 인터페이스를 분리해서 만들어보자

### MemberService

```java
public interface MemberService {

    // 회원가입
    public Long signUp(MemberSignUpRequestDto requestDto) throws Exception;
}
```

------

### MemberServiceImpl

```java
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    @Override
    public Long signUp(MemberSignUpRequestDto requestDto) throws Exception {

        if (memberRepository.findByEmail(requestDto.getEmail()).isPresent()){
            throw new Exception("이미 존재하는 이메일입니다.");
        }

        if (!requestDto.getPassword().equals(requestDto.getCheckedPassword())){
            throw new Exception("비밀번호가 일치하지 않습니다.");
        }

        Member member = memberRepository.save(requestDto.toEntity());
        member.encodePassword(passwordEncoder);

        member.addUserAuthority();
        return member.getId();
    }
}
```

`Exception` 핸들러는 없다치고 `Exception`을 사용하겠다.
Member에 미리 만들어 놓은 `encodePassword` 메서드를 사용해서 비밀번호를 `암호화`한다.

------

### MemberController

```java
@RequiredArgsConstructor
@RequestMapping("/member")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final MemberRepository memberRepository;

    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public Long join(@Valid @RequestBody MemberSignUpRequestDto request) throws Exception {
        return memberService.signUp(request);
    }
```

이걸로 회원가입은 끝이났다. 로그인을 구현해보자.

------

## 로그인

로그인을 하기 위해서 `SecurityConfig` 클래스를 만들어 줍니다.

```java
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin().disable()
                .httpBasic().disable()
                .cors().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/member/login").permitAll()
                .antMatchers("/member/join").permitAll()
                .antMatchers("/member").hasRole("USER")
                .anyRequest().authenticated();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    private static final String[] AUTH_WHITELIST = {
            "/v2/api-docs",
            "/v3/api-docs/**",
            "/configuration/ui",
            "/swagger-resources/**",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/file/**",
            "/image/**",
            "/swagger/**",
            "/swagger-ui/**",
            "/h2/**"
    };

    // 정적인 파일 요청에 대해 무시
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(AUTH_WHITELIST);
    }
}
```

------

### SecurityUtil

```java
public class SecurityUtil {
    public static String getLoginUsername(){
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        return user.getUsername();
    }
}
```

------

### CustomUserDetailsService

```java
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
    }

}
```

------

### JwtTokenProvider

```java
@RequiredArgsConstructor
@Component
public class JwtTokenProvider {
    private String secretKey =
            "c2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQtc2lsdmVybmluZS10ZWNoLXNwcmluZy1ib290LWp3dC10dXRvcmlhbC1zZWNyZXQK";

    // 토큰 유효시간 168 시간(7일)
    private long tokenValidTime = 1440 * 60 * 7 * 1000L;
    private final UserDetailsService userDetailsService;

    // 객체 초기화, secretKey 를 Base64로 인코딩합니다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(String userPk, List<String> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key/value 쌍으로 저장됩니다.
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + tokenValidTime)) // set Expire Time
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘
                // signature 에 들어갈 secret 값 세팅
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져옵니다. "X-AUTH-TOKEN" : "TOKEN값'
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}
```

------

### JwtAuthenticationFilter

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtAuthenticationProvider;

    public JwtAuthenticationFilter(JwtTokenProvider provider) {
        jwtAuthenticationProvider = provider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtAuthenticationProvider.resolveToken(request);

        if (token != null && jwtAuthenticationProvider.validateToken(token)) {
            Authentication authentication = jwtAuthenticationProvider.getAuthentication(token);

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);

    }
}
```

------

### MemberService 추가

```java
public String login(Map<String, String> members);
```

------

### MemberServiceImpl 추가

```java
@Override
public String login(Map<String, String> members) {

Member member = memberRepository.findByEmail(members.get("email"))
		.orElseThrow(() -> new IllegalArgumentException("가입되지 않은 Email 입니다."));

String password = members.get("password");
if (!member.checkPassword(passwordEncoder, password)) {
	throw new IllegalArgumentException("잘못된 비밀번호입니다.");
}

List<String> roles = new ArrayList<>();
roles.add(member.getRole().name());

return jwtTokenProvider.createToken(member.getUsername(), roles);
```

------

### MemberController

```java
@PostMapping("/login")
    public String login(@RequestBody Map<String, String> member) {
        return memberService.login(member);
    }
```

------

## 결과

![img](https://velog.velcdn.com/images/nyong_i/post/dd23714b-ce27-4a22-bab5-2505c1d7d3ba/image.png)



# #6 사용자

![post-thumbnail](https://velog.velcdn.com/images/nyong_i/post/665cc1e8-b4ef-4402-b29f-b1bed7184b99/image.png)

스프링 시큐리티를 개발에 사용하긴 하는데, 뭔가 어정쩡하게 알고 사용하는 것 같아 다시 딥하게 공부해봅니다..

## 스프링 시큐리티의 인증 프로세스

스프링 시큐리티를 알려면 먼저 어떤 순서로 인증하는지 인증 과정을 알아야겠죠? 인증 과정은 아래와 같습니다.

![img](https://velog.velcdn.com/images/nyong_i/post/374a6e13-24b5-4954-b9d1-0fb8490113dd/image.png)

1. 클라이언트가 리퀘스트
2. 인증 필터가 요청을 가로챔
3. 인증 책임이 인증 관리자에게 위임됨
4. 인증 관리자는 인증 논리를 구현하는 인증 공급자를 이용
5. 인증 공급자는 사용자 세부 정보 서비스로 사용자를 찾고 암호 인코더로 암호 검증
6. 인증 결과가 필터에 반환됨
7. 인증된 엔티티의 세부 정보가 시큐리티 컨텍스트에 저장됨

![img](https://velog.velcdn.com/images/nyong_i/post/03254662-0f3f-40fd-90de-35eba132ac22/image.png)

~~개가튼거~~ 아직까지는 무슨말인지 정확히 이해하기 쉽지 않습니다.

이 과정을 거치는데에 스프링 시큐리티가 자동으로 구성하는 빈이 있어요. `UserDetailsService`와 `PasswordEncoder`인데요. 정확히 따지면 스프링 시큐리티가 아니라 스프링 부트가 자동으로 구성해주는 거에요.

인증 공급자는 이런 빈을 써서 사용자를 찾고 암호를 확인해서 인증 과정을 진행해요.

근데 아까부터 인증 관리자, 인증 공급자라고 하는데

- 인증 관리자 : 말 그대로 인증을 관리함(인증 공급자한테 책임 전가후 결과값만 가져옴)
- 인증 공급자 : 인증 관리자로부터 위임 받은 인증을 판단한(인증 성공/실패)

뭐 회사로 따지면 팀장과 직원 같은 느낌..?

이제 위에서 자동으로 생성된다고 한 빈들에 대해 더 자세히 알아볼게요.

------

## 스프링 시큐리티의 기본 구성 재정의

스프링 시큐리티를 사용하는데, UserDetailsService와 PasswordEncoder를 Configuration 클래스에 빈으로 등록해주지 않았다면 아래의 두 가지 이유로 엔드포인트에 접근할 수 없을텐데요.

1. 사용자가 없다.
2. PasswordEncoder가 없다.

뭐 사용자가 없는건 인증하는데에 문제가 있을수는 있지만, 비밀번호를 암호화하는 인터페이스는 인증하는데에 무슨 상관일까요?

스프링 시큐리티는 기본적으로 암호를 관리하는 방법을 지정해줘야해요. 그래서 패스워드 암호화 방식을 정해주지 않으면 스프링 시큐리티는 암호를 관리하는 방식을 모르기 때문에 에러로 자신의 마음을 표출하죠.

보통의 애플리케이션들은 HttpBasic 인증을 잘 사용하지 않아요. 적합하지도 않고요. 애플리케이션의 엔드포인트마다의 접근 권한을 관리하고 싶다면 WebSecurityConfigurerAdapter를 상속받아 메소드를 재정의해주면 돼요.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
    protected void configure(HttpSecurity http) throws Exception {
	}
    
}
```

여기에서 기본적으로 빈들을 재정의할 수도 있죠.

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
	@Override
    protected void configure(HttpSecurity http) throws Exception {
	}
    
}
```

이렇게요.

여기까지 정리해보면 아래와 같습니다.

- 스프링 시큐리티를 디펜던시에 추가하면 스프링 부트가 약간의 기본 구성을 제공한다.
- 인증과 권한 부여를 위한 기본 구성 요소인 UserDetailsService, PasswordEncoder를 구현했다.
- 구성을 작성하는 법은 여러가지가 있지만, 한 애플리케이션에서는 한 방법을 고수해야 코드가 깔끔하고 이해하기 쉬워진다.

------

## 사용자 관리

이번엔 이것들을 자세히 알아볼게요

- UserDetail 인터페이스로 사용자 정의
- 인증 흐름에 UserDetailsService 이용
- UserDetailsService의 맞춤형 구현 만들기

스프링 시큐리티의 가장 기본적인 역할 중 하나인 UserDetailsService를 제대로 이해하기 위한 장이라고 보면 되겠습니다.

그 전에 간단한 인터페이스들을 살펴봅시다.

- UserDetails - 스프링 시큐리티에서 정의하는 사용자
- GrantedAuthority - 스프링 시큐리티에서 정의하는 사용자 권한

흔히 이 부분들은 스프링 시큐리티에서 사용자 관리라고 말하는 흐름의 일부분을 처리해요.

사용자 관리부분의 순서는 다음과 같아요.

1. UserDetails가 사용자를 스프링 시큐리티의 입맛으로 바꿈
2. UserDetailsService가 UserDetails 계약을 이용함
3. UserDetailsManager는 UserDetailsService 계약을 확장함

순서대로 알아봅시다.

------

## 사용자를 스프링 시큐리티의 입맛으로 바꾸기 - UserDetails

UserDetails가 사용자를 스프링 시큐리티의 입맛으로 바꿈이라는 말은 스프링 시큐리티가 사용자를 이해할 수 있도록 애프릴케이션의 사용자를 기술한다는 뜻이에요.

### UserDetails 계약의 정의

계약이라는 말은 인터페이스에 정의된 메서드라고 이해하시면 됩니당.

```java
public interface UserDetails extends Serializable {
	String getUsername();
    String getPassword();
    Collection<? extends GrantedAuthority> getAuthorities();
    boolean isAccountNonExpired();
    boolean isAccountNonLocked();
    boolean isCredentialsNonExpired();
    boolean isEnabled();
}
```

- `String getUsername()` : 사용자 이름을 반환
- `String getPassword()` : 사용자 비밀번호를 반환
- `Collection<? extends GrantedAuthority> getAuthorities()` : 사용자가 수행할 수 있는 작업을 GrantedAuthority 인스턴스의 컬렉션으로 반환
- `boolean isAccountNonExpired()`
- `boolean isAccountNonLocked()`
- `boolean isCredentialsNonExpired()`
- `boolean isEnabled()`

나머지 4개를 묶어서 "사용자 계정을 필요에 따라 활성화 또는 비활성화하는 메소드들"입니다.

순서대로

- 계정 만료
- 계정 잠금
- 자격 증명 만료
- 계정 비활성화

입니다.

이런 기능들이 필요없다면 모두 true를 반환하게 해주면 됩니다.

### UserDetails 구현

```java
@Getter
@RequiredArgsConstructor
public class CustomUserDetail implements UserDetails {

    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> auth = new ArrayList<>();
        auth.add(new SimpleGrantedAuthority(user.getRole().name()));
        return auth;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
```

위에서 잠시 언급했는데, 스프링 시큐리티를 구성하는 방법은 매우 다양합니다. 저는 지금 UserDetails 클래스를 구현했는데, 엔티티가 아닌 다른 클래스를 만들어서 구현했어요.

유저 엔티티와 분리한 이유는 다음과 같아요.

1. User 엔티티에는 JPA 어노테이션, getter, setter가 포함되어있음
2. 단순히 User 엔티티의 속성을 재정의하는 코드들과 섞여 혼란을 줄 수 있음
3. 다른 엔티티들에 대한 관계가 있음

만약 제가 User 엔티티에 UserDetails를 impliments 받았다면 위와 같은 상황이 생겼을텐데, 그 이유는 두 책임을 하나의 클래스에 몰빵했기 때문이에요.

이렇게 책임을 분리하게 되면, 유저 엔티티에는 JPA 엔티티 책임만 남아있으니 훨씬 코드가 간결하고 보기 좋겠죠.

당연히 이것도 최선의 방법이 아닐 수 있습니다. 스프링 시큐리티를 구성하는데에는 많은 방법이 있으니까요.

------

## 스프링 시큐리티가 사용자를 관리하는 방법을 지정하기 - UserDetailsService

앞의 절에서 UserDetails의 계약을 구현해 스프링 시큐리티가 이해할 수 있도록 사용자를 정의했어요.

그렇다면 스프링 시큐리티는 사용자를 어디서 어떻게 가져올까요?

### UserDetailsService 계약의 이해 - loadUserByUsername( )

UserDetailsService 인터페이스는 단 하나의 기능만하는 단 한가지 메서드만 포함해요.

```java
public interface UserDetailsService {
	
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

}
```

인증 구현은 `loadUserByUsername(String username)` 메서드를 호출해 주어진 사용자 이름을 가진 사용자의 세부 정보를 얻어요.

여기에서 매개변수로 받는 username은 당연히 유니크한 값이어야해요. 그리고 보시다 싶이 반환값은 UserDetails 계약의 구현입니다. 사용자 이름이 존재하지 않는다면 `UsernameNotFoundException`를 던지죠.

### UserDetailsService 계약 구현

```java
@RequiredArgsConstructor
@Slf4j
@Service
public class CustomUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .map(CustomUserDetail::new)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }
}
```

저처럼 JPA를 사용하셨다면 이런식으로 사용할 수도 있어요.

이렇게 구현을 마쳤다면 스프링 시큐리티가 UserDetailsService를 이용해 사용자를 관리할 수 있게 돼요.

### 사용자 인증 흐름

마지막으로 스프링 시큐리티에서의 사용자 인증 흐름을 알아볼게요.

1. AuthenticationManager가 AutheticationProvider에게 인증 책임을 전가합니다.
2. AutheticationProvider는 UserDetailsService의 loadUserByUsername 메서드를 사용해서 사용자가 존재하는지 하지 않는지 판단합니다. 만약 존재하지 않는다면 UsernameNotFoundException를 던집니다. 존재한다면 3번으로 넘어갑니다.
3. AuthenticationProvider에게 사용자 세부 정보를 반환합니다.
4. AuthenticationProvider가 PasswordEncoder에게 암호가 일치하는지 일치하지 않는지 물어보러 갑니다. 일치하지 않는다면 BadCredentialsException을 던집니다. 일치한다면 인증을 반환합니다.

AuthenticationManager -> AutheticationProvider -> UserDetailsService -> AutheticationProvider -> PasswordEncoder -> 인증 완료 순이죠.

여기까지가 스프링 시큐리티의 큰 사용자 인증의 흐름입니다. 이 다음과정은 다음 글에서 자세히 적도록 하겠습니다.





# #7 인증 구현

![post-thumbnail](https://velog.velcdn.com/images/nyong_i/post/9841d1b9-519c-44ce-bba8-41069f66ef36/image.png)

## AuthenticationProvider의 이해

엔터프라이즈 애플리케이션에서는 사용자 이름과 암호 기반의 기본 인증 구현이 적합하지 않을 수 있어요. 예를 들면 지문 인식으로 인증할 수도 있고, 요즘 많이 사용하는 Face ID와 같은 인증도 있죠.

일반적으로 프레임워크는 가장 많이 이용되는 구현을 지원하지만 가능한 모든 시나리오를 하결할 수는 없어요.

스프링 시큐리티에서는 AuthenticationProvider 계약으로 모든 맞춤형 인증 논리를 정의할 수 있어요.

- 스프링 시큐리티가 인증 이벤트를 나타내는 방법
- 인증 논리를 담당하는 AuthenticationProvider 계약 파보기
- AuthenticationProvider 계약을 구현하는 예제에서 인증 논리 작성해보기

------

## 인증 프로세스 중 요청 나타내기

AuthenticationProvider를 구현하려면 먼저 인증 인벤트 자체를 나타내는 방법을 이해해야 합니다.

Authentication은 인증이라는 이름이 의미하듯이 인증 프로세스의 필수 인터페이스에요. Authentication 인터페이스는 인증 요청 이벤트를 나타내며 애플리케이션에 접근을 요청한 엔티티의 세부 정보를 담아요.

인증 요청 이벤트와 관련한 정보는 인증 프로세스 도중 또는 이후에 이용할 수 있어요. 여기에서 관련한 정보란 접근을 요청하는 사용자를 뜻하는데, 스프링 시큐리티에서는 이 사용자를 주체(Principal)라고 불러요.

스프링 시큐리티의 Authentication 계약은 주체만 나타내는 것이 아니라 인증 프로세스 완료 여부, 권한의 컬렉션 같은 정보를 추가로 가져요.

Authentication 인터페이스 디자인은 아래와 같아요.

```java
public interface Authentication extends Principal, Serializable {
        Collection<? extends GrantedAuthority> getAuthorities();
        Object getCredentials();
        Object getDetails();
        Object getPrincipal();
        boolean isAuthenticated();
        void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException;
    }
```

현재 이 계약에서 중요한 메서드는 아래 3가지에요.

- `isAuthenticated()` - 인증 프로세스가 끝났으면 true를 반환하고, 아직 진행 중이면 false를 반환해요.
- `getCredentials()` - 인증 프로세스에 이용된 암호나 비밀을 반환한다.
- `getAuthorities()` - 인증된 요청에 허가된 권한의 컬렉션을 반환한다.

------

### 맞춤형 인증 논리 구현

스프링 시큐리티의 AuthenticationProvider는 인증 논리를 처리한다. AuthenticationProvider 인터페이스의 기본 구현은 시스템의 사용자를 찾는 책임을 UserDetailsService에 위임하고 PasswordEncoder로 인증 프로세스에서 암호를 관리해요.

맞춤형 인증 논리라는것은 각 인증마다 인증을 진행할 수 있는 AuthenticationProvider가 다르다는 것이에요.

예를 들면 카드로 인증을 하려고하면 카드 인증 공급자에게 인증을 받아야해요.
만약 카드로 인증을 하려고하는데 열쇠 인증 공급자에게 인증을 받으려고하면 당연히 받을 수 없죠.

이렇듯 각 인증마다 맞는 인증 논리가 나뉘어져 있어요.

------

## SecurityContext 써보기

시큐리티 컨텍스트의 작동 방식과 데이터 접근 방법을 알아보고 다양한 스레드 관련 시나리오에서 애플리케이션이 데이터를 관리하는 방법을 분석해보겠습니다.

대개 인증 프로세스가 끝난 후 인증된 엔티티에 대한 세부 정보가 필요할 가능성이 큰데요. 예를 들어 현재 인증된 사용자의 이름이나 권한을 참조해야 할 수 있습니다.

AuthenticationManager는 인증 프로세스를 성공적으로 완료한 후 요청이 유지되는 동안 Authentication 인스턴스를 저장해요. Authentication 객체를 저장하는 인스턴스를 시큐리티 컨텍스트라고 해요.

스프링 시큐리티의 시큐리티 컨텍스트는 SecurityContext 인터페이스로 기술되는데, 아래와 같습니다.

```java
public interface SecurityContext extends Serializable {
	Authentication getAuthentication();
	void setAuthentication(Authentication authentication);
}
```

이 계약 정의에서 볼 수 있듯이 SecuritContext의 주 책임은 Authentication 객체를 저장하는 것이에요. 그리고 이 SecurityContext를 관리하는 객체가 SecurityContextHolder라고 해요.

이 SecurityContextHolder는 매우 유용하게 사용되는데요. 현재 로그인한 사용자에 대한 정보를 얻고 싶을 때 많이 사용해요.

```java
public class SecurityUtil {
    public static String getLoginUserEmail() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }
}
```

이렇게 사용하면 현재 인증된 사용자의 아이디를 가져올 수 있어요.