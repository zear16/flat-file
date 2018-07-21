package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class AlignLeft {

   @FlatFileField(offset = 0, length = 10)
   private String field;

   public String getField() {
      return field;
   }

   public void setField(String field) {
      this.field = field;
   }
}
