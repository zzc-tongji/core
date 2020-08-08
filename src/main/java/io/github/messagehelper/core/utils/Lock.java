package io.github.messagehelper.core.utils;

public class Lock {
  private int readCounter;
  private int writeCounter;

  public Lock() {
    readCounter = 0;
    writeCounter = 0;
  }

  public void readIncrease() {
    readCounter += 1;
  }

  public void readDecrease() {
    readCounter -= 1;
  }

  public void writeIncrease() {
    writeCounter += 1;
  }

  public void writeDecrease() {
    writeCounter -= 1;
  }

  public boolean isReadLocked() {
    return readCounter > 0;
  }

  public boolean isWriteLocked() {
    return writeCounter > 0;
  }
}
