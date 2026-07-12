package com.exercise.manager.domain;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Member {

        @Id
        private String id;
        private String password;
        private String name;
        private Double weight; // 체중(kg) - 칼로리 계산용, 선택 입력
        private Double height; // 키(cm) - 프로필/BMI용, 선택 입력

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getPassWord() {
            return password;
        }
        public void setPassWord(String passWord){
            this.password = passWord;
        }
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Double getWeight() {
            return weight;
        }

        public void setWeight(Double weight) {
            this.weight = weight;
        }

        public Double getHeight() {
            return height;
        }

        public void setHeight(Double height) {
            this.height = height;
        }

        // 화면 표시용: 체중/키가 둘 다 있을 때만 계산되는 BMI
        public Double getBmi() {
            if (weight == null || height == null || height <= 0) return null;
            double h = height / 100.0;
            return weight / (h * h);
        }
}
