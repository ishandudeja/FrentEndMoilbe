package com.example.northwinddb;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CardView cvsalescard, cvclientscard, cvmarketshare, cvsalesforce;
    private TextView txtcustomers, txtcountries, txtproducts, txtsellers;
    private static final String TAG = "MyDBTag";

    private static final String DEFAULT_DRIVER = "oracle.jdbc.driver.OracleDriver";
    private static final String DEFAULT_URL = "jdbc:oracle:thin:@info706.cwwvo42siq12.ap-southeast-2.rds.amazonaws.com:1521:ORCL";
    private static final String DEFAULT_USERNAME = "19477870";
    private static final String DEFAULT_PASSWORD = "Grimorio12!";

    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cvsalescard = (CardView)findViewById(R.id.salescard);
        cvclientscard = (CardView)findViewById(R.id.clientscard);
        cvmarketshare = (CardView)findViewById(R.id.marketshare);
        cvsalesforce = (CardView)findViewById(R.id.salesforce);

        cvsalescard.setOnClickListener(this);
        cvclientscard.setOnClickListener(this);
        cvmarketshare.setOnClickListener(this);
        cvsalesforce.setOnClickListener(this);


        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        setUpChart();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()){
            case R.id.salescard : i = new Intent(this, salesgraph.class); startActivity(i); break;
            case R.id.clientscard : i = new Intent(this, clientgraph.class); startActivity(i);break;
            case R.id.marketshare : i = new Intent(this, marketgraph.class); startActivity(i);break;
            case R.id.salesforce : i = new Intent(this, sellersgraph.class); startActivity(i);break;

        }
    }


    public static Connection createConnection(String driver, String url, String username, String password) throws ClassNotFoundException, SQLException {

        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }

    public static Connection createConnection() throws ClassNotFoundException, SQLException {
        return createConnection(DEFAULT_DRIVER, DEFAULT_URL, DEFAULT_USERNAME, DEFAULT_PASSWORD);
    }

    public void setUpChart(){
        Log.i(TAG, "Running Query");
        txtcustomers = (TextView)findViewById(R.id.textcustomers);
        txtcountries = (TextView)findViewById(R.id.textcountries);
        txtproducts= (TextView)findViewById(R.id.textproducts);
        txtsellers= (TextView)findViewById(R.id.textsellers);
        try {
            String sql = "select count(*) cuantos from customers";
            this.connection = createConnection();
            Statement stmt = connection.createStatement();
            Log.i(TAG, sql);
            ResultSet rs = stmt.executeQuery(sql);

            while (rs.next()){
                txtcustomers.setText(rs.getString(1));
            }
            connection.close();

            sql = "select count(distinct ship_country) cuantos from ORDERS";
            this.connection = createConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()){
                txtcountries.setText(rs.getString(1));
            }
            connection.close();

            sql = "select count(*) cuantos from products";
            this.connection = createConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()){
                txtproducts.setText(rs.getString(1));
            }
            connection.close();

            sql = "select count(*) cuantos from employees";
            this.connection = createConnection();
            stmt = connection.createStatement();
            rs = stmt.executeQuery(sql);

            while (rs.next()){
                txtsellers.setText(rs.getString(1));
            }


            connection.close();
        }
        catch (Exception e){
            Log.i(TAG, "ERROR !!!  " + e.getMessage());
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            Log.i(TAG, "ERROR Detail !!!  " + sw.toString());

        }




    }

}
