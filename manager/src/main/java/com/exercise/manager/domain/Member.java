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
}
