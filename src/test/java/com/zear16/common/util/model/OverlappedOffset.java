package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class OverlappedOffset {

   @FlatFileField(offset = 10, length = 10)
   private String firstParameter;

   @FlatFileField(offset = 5, length = 6)
   private String secondParameter;

   public String getFirstParameter() {
      return firstParameter;
   }

   public void setFirstParameter(String firstParameter) {
      this.firstParameter = firstParameter;
   }

   public String getSecondParameter() {
      return secondParameter;
   }

   public void setSecondParameter(String secondParameter) {
      this.secondParameter = secondParameter;
   }
}
