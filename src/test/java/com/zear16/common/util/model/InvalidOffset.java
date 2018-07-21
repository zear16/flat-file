package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class InvalidOffset {

   @FlatFileField(offset = -2, length = 1)
   private String firstElement;

   public String getFirstElement() {
      return firstElement;
   }

   public void setFirstElement(String firstElement) {
      this.firstElement = firstElement;
   }
}
