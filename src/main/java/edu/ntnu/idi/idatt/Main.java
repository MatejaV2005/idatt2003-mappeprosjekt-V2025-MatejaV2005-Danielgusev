package edu.ntnu.idi.idatt;

import java.util.Random;

public class Main {

  public static void AreYouGay() {
    boolean gay;
    gay = false;
    Random random = new Random();
    int number = random.nextInt(10) + 1;
    if (number > 4) {
      gay = true;}

    if (gay) {
      System.out.println("You are gay");
    } else {
      System.out.println("You are not gay");
    }
  }
  public static void main(String[] args) {
    System.out.println("Hello world!");
    AreYouGay();
  }
}