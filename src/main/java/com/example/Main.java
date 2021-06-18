/*
 * Copyright 2002-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import jdk.jfr.Registered;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Map;

@Controller
@SpringBootApplication
public class Main {

  @Value("${spring.datasource.url}")
  private String dbUrl;

  @Autowired
  private DataSource dataSource;

  public static void main(String[] args) throws Exception {
    SpringApplication.run(Main.class, args);
  }

  @RequestMapping("/")
  String index(Map<String, Object> model) {
  try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS squares (id serial, boxname varchar(20), height int, width int, color varchar(20), outlined boolean)");
      //String sql = "INSERT INTO squares (boxname,height,width,color,outlined) VALUES ('bob',10,10,'black',true)";
      //stmt.executeUpdate(sql);
      ResultSet rs = stmt.executeQuery("SELECT * FROM squares");
      ArrayList<String> output = new ArrayList<String>();
      while (rs.next()) {
        output.add("Read from DB: " + rs.getString("boxname") + rs.getInt("height") + rs.getInt("Width") + rs.getString("color") + rs.getBoolean("outlined"));
      }

      model.put("boxes", output);
      return "index";
    } 
  catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }

  @GetMapping(
    path = "/person"
  )
  public String getPersonForm(Map<String, Object> model){
    Person person = new Person();  // creates new person object with empty fname and lname
    model.put("person", person);
    return "person";
  }

  @PostMapping(path = "/person",consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
  public String handleBrowserPersonSubmit(Map<String, Object> model, Person person) throws Exception {
    // Save the person data into the database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS people (id serial, fname varchar(20), lname varchar(20))");
      String sql = "INSERT INTO people (fname,lname) VALUES ('" + person.getFname() + "','" + person.getLname() + "')";
      stmt.executeUpdate(sql);
      System.out.println(person.getFname() + " " + person.getLname());
      return "redirect:/person/success";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }
  }


  @GetMapping(path = "/AddSquare")
  public String getAddSquareForm(Map<String, Object> model){
    Square square = new Square();  // creates new person object with empty fname and lname
    model.put("square", square);
    return "AddSquare";
  }

  @PostMapping(
    path = "/AddSquare",
    consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE}
  )
  public String handleBrowserSquareSubmit(Map<String, Object> model, Square square) throws Exception {
    // Save the person data into the database
    try (Connection connection = dataSource.getConnection()) {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate("CREATE TABLE IF NOT EXISTS squares (id serial, boxname varchar(20), height int, width int, color char(7), outlined boolean)");
      String sql = "INSERT INTO squares (boxname,height,width,color,outlined) VALUES ('"+ square.getboxname() +"','"+square.getheight()+"','"+ square.getwidth() +"','"+square.getboxcolor()+"',"+square.getoutlined()+")";
      stmt.executeUpdate(sql);
      //System.out.println(person.getFname() + " " + person.getLname());
      return "redirect:/";
    } catch (Exception e) {
      model.put("message", e.getMessage());
      return "error";
    }

  }

  @GetMapping("/person/success")
  public String getPersonSuccess(){
    return "success";
  }

  @Bean
  public DataSource dataSource() throws SQLException {
    if (dbUrl == null || dbUrl.isEmpty()) {
      return new HikariDataSource();
    } else {
      HikariConfig config = new HikariConfig();
      config.setJdbcUrl(dbUrl);
      return new HikariDataSource(config);
    }
  }

}
