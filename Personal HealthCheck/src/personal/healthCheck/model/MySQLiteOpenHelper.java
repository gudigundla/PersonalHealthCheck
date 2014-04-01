package personal.healthCheck.model;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	
	private static final String packageName = "personal.healthCheck.activities";
	//The Android's default system path of the application database
    private static String dataBasePath = "/data/data/" + packageName +"/databases/";
    private static String dataBaseName = "PersonalHealthCheck.sqlite";
    private SQLiteDatabase myDataBase; 
    
    public SQLiteDatabase getMyDataBase() {
		return myDataBase;
	}

	private final Context myContext;
	
	
	public MySQLiteOpenHelper(Context context) {
		super(context, dataBaseName, null, 1);
		this.myContext=context;
	}
	
	

	
    //Creates a empty database on the system and rewrites it with your own database.
    public void createDataBase() throws IOException{
 
    	boolean dbExist = checkDataBase();
 
    	if(dbExist){
    		//do nothing - database already exist
    		Log.d("Personal HealthChecker-4", "Database already exist");
    	}else{
 
    		//By calling this method and empty database will be created into the default system path
            //of your application so we are gonna be able to overwrite that database with our database.
        	this.getWritableDatabase();
 
        	try {
 
    			copyDataBase();
    			Log.d("Personal HealthChecker-5", "Database Copied !!");
 
    		} catch (Exception e) {
 
    			Log.d("Personal HealthChecker-6", e.toString());
        		throw new Error("Error copying database");
 
        	}
    	}
     }//method
    
    /**
     * Check if the database already exist to avoid re-copying the file each time you open the application.
     * @return true if it exists, false if it doesn't
     */
    private boolean checkDataBase(){
 
    	SQLiteDatabase checkDB = null;
 
    	try{
    		String myPath = dataBasePath + dataBaseName;
    		checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
 
    	}catch(SQLiteException e){
    		Log.d("Personal HealthChecker-7","Database does not exist !!" + e.toString());
    		//database does't exist yet.
 
    	}
 
    	if(checkDB != null){
 
    		checkDB.close();
 
    	}
 
    	return checkDB != null ? true : false;
    }//method
    
    /**
     * Copies your database from your local assets-folder to the just created empty database in the
     * system folder, from where it can be accessed and handled.
     * This is done by transfering bytestream.
     * */
    private void copyDataBase(){
 
    	//Open your local db as the input stream
    	InputStream myInput;
		try {
			myInput = myContext.getAssets().open("PersonalHealthCheck.sqlite");
		 
    	//InputStream myInput = 
    	// Path to the just created empty db
    	String outFileName = dataBasePath + dataBaseName;
 
    	//Open the empty db as the output stream
    	
			OutputStream myOutput;
			myOutput = new FileOutputStream(outFileName);

    	//transfer bytes from the inputfile to the outputfile
    	byte[] buffer = new byte[1024];
    	int length;
			while ((length = myInput.read(buffer))>0){
				myOutput.write(buffer, 0, length);
			}
			//Close the streams	
			myOutput.flush();
	    	myOutput.close();
	    	myInput.close();
    	}
		catch (FileNotFoundException e) {
			Log.d("Personal HealthChecker-9",e.toString());		}

		catch (IOException e) {
			Log.d("Personal HealthChecker-10",e.toString());     }
		
		catch (Exception e) {
			Log.d("Personal HealthChecker-8",e.toString());     }
    }
 
    public void openDataBase() throws SQLException{
    	//Open the database
        String myPath = dataBasePath + dataBaseName;
    	myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }
 
    @Override
	public synchronized void close() {
 
    	    if(myDataBase != null)
    		    myDataBase.close();

    	    super.close();
			Log.d("Personal HealthChecker-5", "Database Closed !!"); 
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}
}
