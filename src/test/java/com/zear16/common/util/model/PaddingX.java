package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class PaddingX {

   @FlatFileField(offset = 0, length = 10, padding = 'X')
   private String field;

   public String getField() {
      return field;
   }

   public void setField(String field) {
      this.field = field;
   }

}
