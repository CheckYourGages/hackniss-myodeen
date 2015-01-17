package util;

public class ArrayStack{

    private class Node{
        int[] array;
        Node next;
        Node(int[] array){this.array=array;}
        void setNext(Node next){this.next=next;}
        int[] getArray(){return this.array;}
        Node getNext(){return this.next;}
    }

    private int count;
    private Node head;

    public ArrayStack(){
        head = null;
        count = 0;
    }

    public boolean isEmpty(){return (count==0) && (head==null);}

    public int size(){return count;}

    public void push(int[] array){
        Node created = new Node(array);
        if(head==null){
            head=created;
            count++;
        }
        else{
            created.setNext(head);
            head=created;
            count++;
        }
    }

    public int[] pop(){
        if(isEmpty()){
            return null;
        }
        else{
            int[] val = head.getArray();
            head=head.getNext();
            count--;
            return val;
        }
    }

    public int[] peek(){
        if(!isEmpty()){
            return head.getArray();
        }
        else
            return null;
    }

    public void clear(){
        head=null;
        count=0;
    }
}
