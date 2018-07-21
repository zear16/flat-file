package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class InvalidLength {

   @FlatFileField(offset = 0, length = 0)
   String parameter;

   public String getParameter() {
      return parameter;
   }

   public void setParameter(String parameter) {
      this.parameter = parameter;
   }
}
