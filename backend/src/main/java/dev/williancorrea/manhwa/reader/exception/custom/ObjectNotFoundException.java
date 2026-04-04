package dev.williancorrea.manhwa.reader.exception.custom;

public class ObjectNotFoundException extends RuntimeException{

  public ObjectNotFoundException(String message){
    super(message);
  }
}