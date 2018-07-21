package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class GetterNotFound {

   @FlatFileField(offset =  0, length = 1, getter = "getFirstParamX")
   private String firstParam;

   public void setFirstParam(String firstParam) {
      this.firstParam = firstParam;
   }
}
