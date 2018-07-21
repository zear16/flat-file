package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;
import com.zear16.common.util.FlatFileRecord;

@FlatFileRecord(length = 255)
public class RecordPadding {

   @FlatFileField(offset = 10, length = 20)
   private String field1;

   @FlatFileField(offset = 100, length = 50)
   private String field2;

   public String getField1() {
      return field1;
   }

   public void setField1(String field1) {
      this.field1 = field1;
   }

   public String getField2() {
      return field2;
   }

   public void setField2(String field2) {
      this.field2 = field2;
   }
}
