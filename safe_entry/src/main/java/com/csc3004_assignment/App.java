package com.csc3004_assignment;

public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello World!" );
        SafeEntryDatabase db = new SafeEntryDatabase();
        db.checkIn("S1234567A", "Ali", "Home", "192.168.68.10");
    }
}
