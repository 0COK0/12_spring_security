package com.ohgiraffers.sessionsecurity.common;

public enum UserRole {
    // 상수 집함(상수를 파이널붙여서 사용했는데 이런식으로 이넘 클래스를 만들 수 있다.)
    // 파이널의 문제점을 보안하기 위해 만들어진게 이넘 타입()

    USER("USER"),
    ADMIN("ADMIN");

    private String role;

    UserRole(String role) {
        this.role = role;
    }

    // 내가 가지고 있는 권한이 어떤건지
    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "UserRole{" +
                "role='" + role + '\'' +
                '}';
    }
}
