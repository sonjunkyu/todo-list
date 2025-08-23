# To-Do List Application

이 프로젝트는 Spring Boot와 Spring Security를 사용하여 구현한 간단한 할 일 목록 웹 애플리케이션입니다. 사용자 인증 및 개인별 할 일 목록 관리 기능을 제공합니다.

## ✨ 주요 기능

- **사용자 인증**: 회원가입 및 로그인 기능
- **할 일 관리**: 할 일(Todo) 생성, 조회, 수정, 삭제 (CRUD)
- **개인화**: 사용자별로 자신의 할 일 목록만 조회 및 관리

## 🛠 기술 스택

- **Backend**: Java 17, Spring Boot 3.5.5, Spring Security, Spring Data JPA
- **Database**: MySql
- **Frontend**: HTML, CSS, JavaScript
- **Build Tool**: Gradle

## 🚀 시작하기

### 사전 요구사항

- Java 17 이상
- Gradle 8.0 이상

### 설치 및 실행

1. **프로젝트 복제**
git clone https://github.com/your-username/todo.git
cd todo

2. **프로젝트 빌드 및 실행**
- Windows: `gradlew.bat bootRun`
- macOS/Linux: `./gradlew bootRun`

3. **접속**
- 애플리케이션 실행 후, 웹 브라우저에서 `http://localhost:8080` 주소로 접속하세요.

## 📦 ER Diagram
<img width="451" height="257" alt="image" src="https://github.com/user-attachments/assets/a5e61ee4-1826-400a-9247-3d3e16905613" />

## 🔒 보안 (Security)
이 프로젝트는 Spring Security를 사용하여 폼 기반 로그인과 세션 방식으로 사용자를 인증하고 권한을 관리합니다.

- **인증 방식**: 사용자가 제출한 아이디와 비밀번호를 'MemberDetailsService'가 DB 정보와 비교하며, 비밀번호는 'BCryptPasswordEncoder'를 통해 안전하게 검증됩니다.
- **접근 제어**: '/login', '/signup' 등 일부 URL을 제외한 '/todos'와 같은 핵심 기능 페이지는 인증된 사용자만 접근할 수 있도록 설정되어 있습니다.
- **세션 관리**: 인증에 성공하면 사용자 정보가 HttpSession에 저장되어 로그인 상태가 유지됩니다.
   
## 🧭 사용 시나리오

1. **회원가입**
- 메인 페이지에서 '회원가입' 버튼을 클릭하여 회원가입 페이지로 이동합니다.
- 사용할 아이디와 비밀번호를 입력하고 '가입' 버튼을 눌러 계정을 생성합니다.

2. **로그인**
- 회원가입 완료 후 또는 메인 페이지에서 '로그인' 버튼을 클릭하여 로그인 페이지로 이동합니다.
- 생성한 아이디와 비밀번호로 로그인합니다.
- 로그인에 성공하면 본인의 할 일 목록 페이지 (`/todos`)로 이동합니다.

3. **할 일(Todo) 관리**
- **작성**: 페이지 상단의 입력란에 새로운 할 일을 입력하고 '추가' 버튼을 눌러 목록에 추가합니다.
- **조회**: 로그인된 사용자의 모든 할 일 목록이 목록 형태로 표시됩니다.
- **완료 처리**: 각 할 일 항목 옆의 체크박스를 클릭하여 완료 상태로 변경하거나 되돌릴 수 있습니다. 완료된 항목은 취소선으로 표시됩니다.
- **수정**: '수정' 버튼을 클릭하면 수정 모드로 변경됩니다. 내용을 수정한 후 '저장' 버튼을 눌러 저장합니다.
- **삭제**: '삭제' 버튼을 클릭하며 해당 할 일을 목록에서 영구적으로 삭제합니다.
