package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class InvalidSetter {

   @FlatFileField(offset = 0, length = 1, setter = "setParameterXXX")
   private String parameter;

   public String getParameter() {
      return parameter;
   }

   public void setParameter(String parameter) {
      this.parameter = parameter;
   }
}
