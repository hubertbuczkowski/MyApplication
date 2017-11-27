package com.example.h_buc.activitytracker;

import java.sql.Driver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import oracle.jdbc.OracleDriver;
import oracle.jdbc.pool.OracleDataSource;


public class dbConnect
{
    public void ConnectionToDB()
    {
        try
        {
            String DB = "cpsg6pu67gts.us-east-2.rds.amazonaws.com";
            //String DB = "androidmysql.cpsg6pu67gts.us-east-2.rds.amazonaws.com";
            //String DB = "androidpost.cpsg6pu67gts.us-east-2.rds.amazonaws.com";
            String usr = "hubert";
            String pass = "hubert1234";
            String port = "5432/androidpost";
            System.out.println("driver");
            //DriverManager.registerDriver (new oracle.jdbc.OracleDriver());
            DriverManager.registerDriver (new org.postgresql.Driver());
            //Class.forName("com.mysql.jdbc.Driver");
            //Class.forName("org.postgresql.Driver");

            Connection con= DriverManager.getConnection("jdbc:postgresql://" + DB + ":" + port  , usr, pass);

            /*Statement stmt=con.createStatement();

            ResultSet rs=stmt.executeQuery("select * from user_pass");
            while(rs.next())
            {
                System.out.println("select nr");
                System.out.println(rs.getInt(1) + "  " + rs.getString(2) + "  " + rs.getString(3));
            }*/

            con.close();

            System.out.println("connected");


        }
        catch(Exception e)
        {
           System.out.println(e);
        }

    }
}

