package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;
import com.zear16.common.util.FlatFileRecord;

@FlatFileRecord(length = 2)
public class OffsetLengthOverflow {

   @FlatFileField(offset = 0, length = 3)
   private String firstParam;

   public String getFirstParam() {
      return firstParam;
   }

   public void setFirstParam(String firstParam) {
      this.firstParam = firstParam;
   }

}
