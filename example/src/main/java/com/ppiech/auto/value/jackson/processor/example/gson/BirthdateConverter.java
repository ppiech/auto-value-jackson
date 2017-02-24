package com.ppiech.auto.value.jackson.processor.example.gson;

import com.ppiech.auto.value.jackson.typeconverters.DateTypeConverter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class BirthdateConverter extends DateTypeConverter {

  private DateFormat mDateFormat;

  public BirthdateConverter(){
    mDateFormat = new SimpleDateFormat("yyyy-MM-dd");
  }

  @Override
  public DateFormat getDateFormat() {
    return mDateFormat;
  }
}
