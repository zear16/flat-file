package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class AlignRight {

   @FlatFileField(offset = 0, length = 10, align = FlatFileField.Align.RIGHT)
   private String field;

   public String getField() {
      return field;
   }

   public void setField(String field) {
      this.field = field;
   }
}
