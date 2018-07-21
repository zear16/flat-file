package com.zear16.common.util.model;

import com.zear16.common.util.FlatFileField;

public class Success {

   @FlatFileField(offset = 4, length = 15)
   private String merchantId;

   @FlatFileField(offset = 32, length = 12)
   private String invoiceNo;

   @FlatFileField(offset = 44, length = 8)
   private String date;

   @FlatFileField(offset = 85, length = 12)
   private String transAmount;

   @FlatFileField(offset = 97, length = 2)
   private String responseCode;

   public String getMerchantId() {
      return merchantId;
   }

   public void setMerchantId(String merchantId) {
      this.merchantId = merchantId;
   }

   public String getInvoiceNo() {
      return invoiceNo;
   }

   public void setInvoiceNo(String invoiceNo) {
      this.invoiceNo = invoiceNo;
   }

   public String getDate() {
      return date;
   }

   public void setDate(String date) {
      this.date = date;
   }

   public String getTransAmount() {
      return transAmount;
   }

   public void setTransAmount(String transAmount) {
      this.transAmount = transAmount;
   }

   public String getResponseCode() {
      return responseCode;
   }

   public void setResponseCode(String responseCode) {
      this.responseCode = responseCode;
   }

}
