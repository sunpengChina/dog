package top.dogtcc.test.server2.dao;

public class TestEntry {

    private TestEntry next = null;

    public TestEntry getNext() {
        return next;
    }

    public void setNext(TestEntry next) {
        this.next = next;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    boolean success;

}
