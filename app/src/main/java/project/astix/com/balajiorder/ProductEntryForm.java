package project.astix.com.balajiorder;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.astix.Common.CommonInfo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.UUID;
import java.util.regex.Pattern;

public class ProductEntryForm extends BaseActivity implements focusLostCalled, View.OnClickListener,CategoryCommunicator,InterfaceClass {

    //LocationSetting
    public LocationManager locationManager;
    boolean isSettingAlertOpen=false;
    int countSubmitClicked=0;
    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    TableLayout tbl1_dyntable_For_OrderDetails;
    //Location Variables
    public  int flgLocationServicesOnOffOrderReview=0,flgGPSOnOffOrderReview=0, flgNetworkOnOffOrderReview=0,flgFusedOnOffOrderReview=0,flgInternetOnOffWhileLocationTrackingOrderReview=0,flgRestartOrderReview=0,flgStoreOrderOrderReview=0;

    public String fnAccurateProvider="";
    public String fnLati="0";
    public String fnLongi="0";
    public String fnAccuracy="0.0";
    String butnClkd="-1";

    LinkedHashMap<String,String> hmapPrdctIdOutofStock=new LinkedHashMap<String,String> ();
    //Invoice table
    public TextView tv_NetInvValue,tvTAmt,tvDis,tv_GrossInvVal,tvFtotal,tvAddDisc,tv_NetInvAfterDiscount,tvAmtPrevDueVAL,etAmtCollVAL
            ,tvAmtOutstandingVAL,tvCredAmtVAL,tvINafterCredVAL,textView1_CredAmtVAL_new,tvNoOfCouponValue,txttvCouponAmountValue;

    public ProductFilledDataModel prdctModelArrayList=new ProductFilledDataModel();
    //CustomKeyboard
    CustomKeyboard mCustomKeyboardNum,mCustomKeyboardNumWithoutDecimal;
    //Product Page Views
    TextView txt_Lststock,img_ctgry,txtVw_schemeApld,txt_RefreshOdrTot;
    EditText ed_search;
    Button btn_orderReview,btn_Save,btn_SaveExit;
    ImageView executionDetails_butn,img_return,btn_bck;
    RecyclerView rv_prdct_detal;
    LinearLayout ll_scheme_detail;
    //Intent Data
    public String storeID,imei,date,pickerDate,SN;
    int flgOrderType=0;

    ProgressDialog mProgressDialog;
    String progressTitle="";

    //Database
     DBAdapterKenya dbengine = new DBAdapterKenya(this);

     //product data from database

     public int StoreCurrentStoreType=0,CheckIfStoreExistInStoreProdcutPurchaseDetails=0,CheckIfStoreExistInStoreProdcutInvoiceDetails=0,StoreCatNodeId=0;
     public String strGlobalOrderID="0",ctgryFirstName,ctgryFirstId,lastStockDate="",distID="",previousSlctdCtgry="";
    List<String> categoryNames;
    LinkedHashMap<String, String> hmapctgry_details;
    LinkedHashMap<String, String> hmapFilterProductList;
    //hmapCtgryPrdctDetail= key=prdctId,val=ProductName
    HashMap<String, String> hmapPrdctIdPrdctName;
    //hmapProductVatTaxPerventage= key =ProductID         value=ProductMRP
    HashMap<String, String> hmapProductMRP;
    HashMap<String, String> hmapProductStandardRate;
    ArrayList<HashMap<String, String>> arrLstHmapPrdct;
    LinkedHashMap<String,Integer> hmapDistPrdctStockCount;
    HashMap<String, String> hmapProductIdLastStock;
    HashMap<String, String> hmapPrdctOdrQty;
    HashMap<String, String> hmapProductLODQty;
    HashMap<String, String> hmapProductIdStock;
    HashMap<String, String> hmapProductVatTaxPerventage;
    HashMap<String, String> hmapProductTaxValue=new HashMap<String, String>();
    HashMap<String, String> hmapProductIdOrdrVal =new HashMap<String, String>();
    HashMap<String, String> hmapCtgryPrdctDetail;




   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_entry_form);
        getDataFromIntent();
        initializeFields();
       getDataFromDatabase();
       setInvoiceTableView();
       img_ctgry.setText("All");

       searchProduct("All","");
       /*if((ctgryFirstName!=null) && (ctgryFirstId!=null) )
       {
           img_ctgry.setText(ctgryFirstName);

           searchProduct(ctgryFirstName,ctgryFirstId);
       }
       else
       {
           img_ctgry.setText("All");

           searchProduct("All","");
       }*/

   }

    private void initializeFields() {

        mCustomKeyboardNum= new CustomKeyboard(this, R.id.keyboardviewNum, R.xml.num );
        mCustomKeyboardNumWithoutDecimal= new CustomKeyboard(this, R.id.keyboardviewNumDecimal, R.xml.num_without_decimal );

        txt_Lststock= (TextView) findViewById(R.id.txt_Lststock);
         executionDetails_butn=(ImageView)findViewById(R.id.txt_execution_Details);
        img_ctgry= (TextView) findViewById(R.id.img_ctgry);
        ed_search=(EditText) findViewById(R.id.ed_search);
        txtVw_schemeApld=(TextView) findViewById(R.id.txtVw_schemeApld);
        txt_RefreshOdrTot=(TextView) findViewById(R.id.txt_RefreshOdrTot);
        btn_orderReview=(Button) findViewById(R.id.btn_orderReview);
        btn_Save=(Button) findViewById(R.id.btn_save);
        btn_SaveExit=(Button) findViewById(R.id.btn_saveExit);
        img_return=(ImageView) findViewById(R.id.img_return);
        btn_bck=(ImageView) findViewById(R.id.btn_bck);
        rv_prdct_detal=(RecyclerView) findViewById(R.id.rv_prdct_detal);
        ll_scheme_detail=(LinearLayout) findViewById(R.id.ll_scheme_detail);
        img_ctgry.setOnClickListener(this);
        btn_bck.setOnClickListener(this);
        btn_orderReview.setOnClickListener(this);
        btn_Save.setOnClickListener(this);
        btn_SaveExit.setOnClickListener(this);
        executionDetails_butn.setOnClickListener(this);
    }

    private void getDataFromIntent() {


        Intent passedvals = getIntent();

        storeID = passedvals.getStringExtra("storeID");
        imei = passedvals.getStringExtra("imei");
        date = passedvals.getStringExtra("userdate");
        pickerDate = passedvals.getStringExtra("pickerDate");
        SN = passedvals.getStringExtra("SN");
        flgOrderType=passedvals.getIntExtra("flgOrderType",0);

    }

    public void getDataFromDatabase()
    {
        dbengine.open();
        StoreCurrentStoreType=Integer.parseInt(dbengine.fnGetStoreTypeOnStoreIdBasis(storeID));
        dbengine.close();
        CheckIfStoreExistInStoreProdcutPurchaseDetails=dbengine.fnCheckIfStoreExistInStoreProdcutPurchaseDetails(storeID);
        CheckIfStoreExistInStoreProdcutInvoiceDetails=dbengine.fnCheckIfStoreExistInStoreProdcutInvoiceDetails(storeID);
        if(CheckIfStoreExistInStoreProdcutPurchaseDetails==1 || CheckIfStoreExistInStoreProdcutInvoiceDetails==1)
        {
            strGlobalOrderID=dbengine.fngetOrderIDAganistStore(storeID);
        }
        else
        {
            strGlobalOrderID= genOutOrderID();
        }
        distID=dbengine.getDisId(storeID);
        hmapDistPrdctStockCount=dbengine.getDistStockCount(distID);
        getCategoryDetail();
        getPrductInfoDetail();
    }

    private void getCategoryDetail()
    {

        hmapctgry_details=dbengine.fetch_Category_List();

        int index=0;
        if(hmapctgry_details!=null)
        {
            categoryNames=new ArrayList<String>();
            LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(hmapctgry_details);
            Set set2 = map.entrySet();
            Iterator iterator = set2.iterator();
            while(iterator.hasNext()) {
                Map.Entry me2 = (Map.Entry)iterator.next();
                categoryNames.add(me2.getKey().toString());
                if(index==1)
                {
                    ctgryFirstName=me2.getKey().toString();
                    ctgryFirstId=me2.getValue().toString();
                }
                index=index+1;
            }
        }


    }

    public String genOutOrderID()
    {
        //store ID generation <x>
        long syncTIMESTAMP = System.currentTimeMillis();
        Date dateobj = new Date(syncTIMESTAMP);
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss", Locale.ENGLISH);
        String VisitStartTS = df.format(dateobj);
        String cxz;
        cxz = UUID.randomUUID().toString();
        /*cxz.split("^([^-]*,[^-]*,[^-]*,[^-]*),(.*)$");*/

        StringTokenizer tokens = new StringTokenizer(String.valueOf(cxz), "-");

        String val1 = tokens.nextToken().trim();
        String val2 = tokens.nextToken().trim();
        String val3 = tokens.nextToken().trim();
        String val4 = tokens.nextToken().trim();
        cxz = tokens.nextToken().trim();



						/*TelephonyManager tManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
						String imei = tManager.getDeviceId();*/
        String IMEIid =  CommonInfo.imei.substring(9);
        //cxz = IMEIid +"-"+cxz;
        cxz = "OrdID" + "-" +IMEIid +"-"+cxz+"-"+VisitStartTS.replace(" ", "").replace(":", "").trim();


        return cxz;
        //-_
    }

    private void setInvoiceTableView() {

        LayoutInflater inflater = getLayoutInflater();
        final View row123 = (View)inflater.inflate(R.layout.activity_detail_scheme, ll_scheme_detail , false);


        tvCredAmtVAL =  (TextView) row123.findViewById(R.id.textView1_CredAmtVAL);
        tvINafterCredVAL =  (TextView) row123.findViewById(R.id.textView1_INafterCredVAL);
        textView1_CredAmtVAL_new = (TextView) row123.findViewById(R.id.textView1_CredAmtVAL_new);


        tv_NetInvValue = (TextView)row123.findViewById(R.id.tv_NetInvValue);
        tvTAmt = (TextView)row123.findViewById(R.id.textView1_v2);
        tvDis = (TextView)row123.findViewById(R.id.textView1_v3);
        tv_GrossInvVal = (TextView)row123.findViewById(R.id.tv_GrossInvVal);
        tvFtotal = (TextView)row123.findViewById(R.id.textView1_v5);
        tvAddDisc =  (TextView)row123.findViewById(R.id.textView1_AdditionalDiscountVAL);
        tv_NetInvAfterDiscount =  (TextView)row123.findViewById(R.id.tv_NetInvAfterDiscount);

        tvAmtPrevDueVAL =  (TextView)row123.findViewById(R.id.tvAmtPrevDueVAL);
        tvAmtOutstandingVAL =  (TextView)row123.findViewById(R.id.tvAmtOutstandingVAL);
        etAmtCollVAL = (EditText)row123.findViewById(R.id.etAmtCollVAL);

        tvNoOfCouponValue = (EditText)row123.findViewById(R.id.tvNoOfCouponValue);
        txttvCouponAmountValue = (EditText)row123.findViewById(R.id.tvCouponAmountValue);
        ll_scheme_detail.addView(row123);


    }

    public void searchProduct(String filterSearchText,String ctgryId)
    {

        if(hmapFilterProductList!=null)
        {
            hmapFilterProductList.clear();
        }

        hmapFilterProductList=dbengine.getFileredProductListMap(filterSearchText.trim(),StoreCurrentStoreType,ctgryId);

        if(hmapFilterProductList.size()>0)
        {
            String[] listProduct=new String[hmapFilterProductList.size()];
            int index=0;
            for(Map.Entry<String,String> entry:hmapFilterProductList.entrySet())
            {
                listProduct[index]=entry.getKey()+"^"+entry.getValue();
                index++;
            }

            OrderAdapter orderAdapter=new OrderAdapter(ProductEntryForm.this,listProduct,hmapFilterProductList,hmapProductStandardRate,hmapProductMRP,hmapProductIdStock,hmapProductIdLastStock,hmapProductLODQty,hmapDistPrdctStockCount,prdctModelArrayList);
            rv_prdct_detal.setAdapter(orderAdapter);
            rv_prdct_detal.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));
        }
        else
        {
            allMessageAlert(ProductEntryForm.this.getResources().getString(R.string.AlertFilter));
        }


    }

    private void allMessageAlert(String message) {
        AlertDialog.Builder alertDialogNoConn = new AlertDialog.Builder(ProductEntryForm.this);
        alertDialogNoConn.setTitle(ProductEntryForm.this.getResources().getString(R.string.genTermInformation));
        alertDialogNoConn.setMessage(message);
        //alertDialogNoConn.setMessage(getText(R.string.connAlertErrMsg));
        alertDialogNoConn.setNeutralButton(ProductEntryForm.this.getResources().getString(R.string.AlertDialogOkButton),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.dismiss();
                        ed_search.requestFocus();
	                     /*if(isMyServiceRunning())
	               		{
	                     stopService(new Intent(DynamicActivity.this,GPSTrackerService.class));
	               		}
	                     finish();*/
                        //finish();
                    }
                });
        alertDialogNoConn.setIcon(R.drawable.info_ico);
        AlertDialog alert = alertDialogNoConn.create();
        alert.show();

    }

    public void getPrductInfoDetail()
    {
        hmapProductIdLastStock=dbengine.fnGetLastStockByDMS_Or_SFA(storeID);
        arrLstHmapPrdct = dbengine.fetch_catgry_prdctsData(storeID, StoreCurrentStoreType);
        lastStockDate=dbengine.fnGetLastStockDate(storeID);
        hmapProductLODQty=dbengine.FetchLODqty(storeID);
        if(arrLstHmapPrdct!=null && arrLstHmapPrdct.size()>0)
        {

            hmapCtgryPrdctDetail = arrLstHmapPrdct.get(0);
            hmapPrdctIdPrdctName = arrLstHmapPrdct.get(5);
            hmapProductVatTaxPerventage = arrLstHmapPrdct.get(8);
            hmapProductMRP = arrLstHmapPrdct.get(9);

            hmapProductStandardRate = arrLstHmapPrdct.get(15);


            hmapProductIdStock=arrLstHmapPrdct.get(18);


        }
        hmapPrdctOdrQty=dbengine.fnGetProductPurchaseListHmap(storeID,strGlobalOrderID);
        if(hmapPrdctOdrQty!=null && hmapPrdctOdrQty.size()>0)
        {
            for(Map.Entry<String,String> entry:hmapPrdctOdrQty.entrySet())
            {
                if(Integer.parseInt(entry.getValue())>0)
                {
                    prdctModelArrayList.setPrdctQty(entry.getKey(),entry.getValue());
                }

            }

        }

    }


    @Override
    public void fcsLstCld(boolean hasFocus, EditText editText) {
        mCustomKeyboardNumWithoutDecimal.hideCustomKeyboard();
        if(!hasFocus)
        {
            EditText edtFcsLst=prdctModelArrayList.getFocusLostEditText();
            fnCreditAndStockCal(-1,edtFcsLst);
            orderBookingTotalCalc();
        }
        else
        {
            if(editText.getTag().toString().contains("etOrderQty"))
            {

                mCustomKeyboardNumWithoutDecimal.registerEditText(editText);
                mCustomKeyboardNumWithoutDecimal.showCustomKeyboard(editText);
            }
            else
            {


            }

        }

    }

    @Override
    public void onClick(View v) {
        EditText ed_LastEditextFocusd=prdctModelArrayList.getLastEditText();
        switch(v.getId())
        {

            case R.id.img_ctgry:
                customAlertStoreList(categoryNames,"Select Category");
                break;
            case R.id.btn_bck:
                fnCreditAndStockCal(5,ed_LastEditextFocusd);
                break;
            case R.id.btn_save:
                fnCreditAndStockCal(1,ed_LastEditextFocusd);
                break;
            case R.id.btn_saveExit:
                fnCreditAndStockCal(2,ed_LastEditextFocusd);
                break;
            case R.id.btn_orderReview:
                fnCreditAndStockCal(0,ed_LastEditextFocusd);
                break;
            case R.id.txt_execution_Details:
                executionData();
                break;


        }
    }


    public void customAlertStoreList(final List<String> listOption, String sectionHeader)
    {

        final Dialog listDialog = new Dialog(ProductEntryForm.this);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        listDialog.setContentView(R.layout.search_list);
        listDialog.setCanceledOnTouchOutside(false);
        listDialog.setCancelable(false);
        WindowManager.LayoutParams parms = listDialog.getWindow().getAttributes();
        parms.gravity = Gravity.CENTER;
        //there are a lot of settings, for dialog, check them all out!
        parms.dimAmount = (float) 0.5;




        TextView txt_section=(TextView) listDialog.findViewById(R.id.txt_section);
        txt_section.setText(sectionHeader);
        TextView txtVwCncl=(TextView) listDialog.findViewById(R.id.txtVwCncl);
        //    TextView txtVwSubmit=(TextView) listDialog.findViewById(R.id.txtVwSubmit);

        final EditText ed_search=(AutoCompleteTextView) listDialog.findViewById(R.id.ed_search);
        ed_search.setVisibility(View.GONE);
        final ListView list_store=(ListView) listDialog.findViewById(R.id.list_store);
        final CardArrayAdapterCategory cardArrayAdapter = new CardArrayAdapterCategory(ProductEntryForm.this,listOption,listDialog,previousSlctdCtgry);

        //img_ctgry.setText(previousSlctdCtgry);





        list_store.setAdapter(cardArrayAdapter);
        //	editText.setBackgroundResource(R.drawable.et_boundary);
        img_ctgry.setEnabled(true);





        txtVwCncl.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                listDialog.dismiss();
                img_ctgry.setEnabled(true);


            }
        });




        //now that the dialog is set up, it's time to show it
        listDialog.show();

    }

    @Override
    public void selectedOption(String selectedCategory, Dialog dialog) {
        dialog.dismiss();
        previousSlctdCtgry=selectedCategory;
        String lastTxtSearch=ed_search.getText().toString().trim();
        img_ctgry.setText(selectedCategory);

        if(hmapctgry_details.containsKey(selectedCategory))
        {
            searchProduct(lastTxtSearch,hmapctgry_details.get(selectedCategory));
        }
        else
        {
            searchProduct(lastTxtSearch,"");
        }

    }

    public void fnCreditAndStockCal(int butnClkd, EditText ed_LastEditextFocusd)
    {

        if(ed_LastEditextFocusd!=null)
        {

            String ProductIdOnClickedEdit=ed_LastEditextFocusd.getTag().toString().split(Pattern.quote("_"))[0];
            String tag=ed_LastEditextFocusd.getTag().toString();
            if(tag.contains("etOrderQty"))
            {
                String prdctQty=   prdctModelArrayList.getPrdctOrderQty(ProductIdOnClickedEdit);

                if(!TextUtils.isEmpty(prdctQty))
                {

                    if ((hmapDistPrdctStockCount!=null) &&(hmapDistPrdctStockCount.containsKey(ProductIdOnClickedEdit)))
                    {
                        if(hmapPrdctIdOutofStock!=null && hmapPrdctIdOutofStock.size()>0) {
                            if (hmapPrdctIdOutofStock.containsKey(ProductIdOnClickedEdit)) {
                                int lastOrgnlQntty = Integer.parseInt(hmapPrdctIdOutofStock.get(ProductIdOnClickedEdit));


                                int netStockLeft = hmapDistPrdctStockCount.get(ProductIdOnClickedEdit) + lastOrgnlQntty;
                                hmapDistPrdctStockCount.put(ProductIdOnClickedEdit, netStockLeft);


                            }
                        }

                            int originalNetQntty=0;
                            if(!TextUtils.isEmpty(ed_LastEditextFocusd.getText().toString()))
                            {
                                originalNetQntty=Integer.parseInt(ed_LastEditextFocusd.getText().toString());
                            }

                            int totalStockLeft=hmapDistPrdctStockCount.get(ProductIdOnClickedEdit);
                            int netStock=totalStockLeft-originalNetQntty;
                            hmapDistPrdctStockCount.put(ProductIdOnClickedEdit,netStock);
                            if(originalNetQntty!=0)
                            {
                                hmapPrdctIdOutofStock.put(ProductIdOnClickedEdit,ed_LastEditextFocusd.getText().toString().trim());
                            }
                            else
                            {
                                hmapPrdctIdOutofStock.remove(ProductIdOnClickedEdit);
                                dbengine.deleteExistStockTable(distID,strGlobalOrderID,ProductIdOnClickedEdit);
                            }
                            if (originalNetQntty>totalStockLeft)
                            {
                                EditText edOrderCurrent=prdctModelArrayList.getLastEditText();
                                if(edOrderCurrent!=null)
                                {
                                    alertForOrderExceedStock(ProductIdOnClickedEdit,edOrderCurrent,ed_LastEditextFocusd,-1);
                                }
                                else
                                {
                                    alertForOrderExceedStock(ProductIdOnClickedEdit,ed_LastEditextFocusd,ed_LastEditextFocusd,-1);
                                }


                            }
                            else
                            {

                                nextStepAfterRetailerCreditBal(butnClkd);



                            }
                        }
                    else
                    {

                        nextStepAfterRetailerCreditBal(butnClkd);


                    }




                }
                else
                {


                    nextStepAfterRetailerCreditBal(butnClkd);


                }

            }


            else
            {

                nextStepAfterRetailerCreditBal(butnClkd);


            }
        }
        else
        {
            nextStepAfterRetailerCreditBal(butnClkd);

        }


    }

    public void alertForOrderExceedStock(final String productOIDClkd, final EditText edOrderCurrent, final EditText edOrderCurrentLast, final int flagClkdButton)
    {
        AlertDialog.Builder alertDialogSubmitConfirm = new AlertDialog.Builder(ProductEntryForm.this);
        alertDialogSubmitConfirm.setTitle(ProductEntryForm.this.getResources().getString(R.string.StockOverbooked));
        int avilabQty=hmapDistPrdctStockCount.get(productOIDClkd)+Integer.parseInt(prdctModelArrayList.getPrdctOrderQty(productOIDClkd));
        alertDialogSubmitConfirm.setMessage(ProductEntryForm.this.getResources().getString(R.string.AvailableQty)+avilabQty +"\n"+ProductEntryForm.this.getResources().getString(R.string.OrderQty)+hmapPrdctOdrQty.get(productOIDClkd)+"\n"+hmapPrdctIdPrdctName.get(productOIDClkd)+" "+getText(R.string.order_exceeds_stock));

        alertDialogSubmitConfirm.setCancelable(false);

        alertDialogSubmitConfirm.setNeutralButton(ProductEntryForm.this.getResources().getString(R.string.Continue), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which)
            {
                if(flagClkdButton!=-1)
                {


                        nextStepAfterRetailerCreditBal(flagClkdButton);


                }

                dialog.dismiss();

            }
        });

        alertDialogSubmitConfirm.setNegativeButton(ProductEntryForm.this.getResources().getString(R.string.ChangeQty), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                edOrderCurrent.clearFocus();
                edOrderCurrentLast.requestFocus();
                dialog.dismiss();
            }
        });

        alertDialogSubmitConfirm.setIcon(R.drawable.info_ico);

        AlertDialog alert = alertDialogSubmitConfirm.create();

        alert.show();


    }


    public void nextStepAfterRetailerCreditBal(int btnClkd)
    {

        if(btnClkd!=5)
        {
            long StartClickTime = System.currentTimeMillis();
            Date dateobj1 = new Date(StartClickTime);
            SimpleDateFormat df1 = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
            String StartClickTimeFinal = df1.format(dateobj1);


            String fileName=imei+"_"+storeID;

            //StringBuffer content=new StringBuffer(imei+"_"+storeID+"_"+"SaveExit Button Click on Product List"+StartClickTimeFinal);
            //File file = new File("/sdcard/MeijiIndirectTextFile/"+fileName);
            File file = new File("/sdcard/"+CommonInfo.TextFileFolder+"/"+fileName);

            if (!file.exists())
            {
                try
                {
                    file.createNewFile();
                }
                catch (IOException e1)
                {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }


            CommonInfo.fileContent=CommonInfo.fileContent+"     "+imei+"_"+storeID+"_"+"SaveExit Button Click on Product List"+StartClickTimeFinal;


            FileWriter fw;
            try
            {
                fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);
                bw.write(CommonInfo.fileContent);
                bw.close();

                dbengine.open();
                dbengine.savetblMessageTextFileContainer(fileName,0);
                dbengine.close();


            }
            catch (IOException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }



        if(btnClkd==0) // order review
        {


            orderBookingTotalCalc();





            progressTitle=ProductEntryForm.this.getResources().getString(R.string.WhileWeSaveExit);
            //  progressTitle="While we save your data then review Order";
            new SaveData().execute("6");
        }

        else if(btnClkd==1) // btn save clkd
        {

            orderBookingTotalCalc();
            progressTitle=ProductEntryForm.this.getResources().getString(R.string.WhileSave);
            new SaveData().execute("1");

        }

        else if(btnClkd==2) // btn clkd save and exit
        {



            orderBookingTotalCalc();
            dbengine.open();
            dbengine.updateflgFromWhereSubmitStatusAgainstStore(storeID, 2);

            if ((dbengine.PrevLocChk(storeID.trim())) )
            {
                dbengine.close();

                orderBookingTotalCalc();

                    progressTitle=ProductEntryForm.this.getResources().getString(R.string.WhileWeSaveExit);
                      new SaveData().execute("2");

                    // new SaveData().execute("2");

            }
            else
            {

                this.butnClkd="2";
                if(flgOrderType==0)
                {
                    LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                    llaaa.locationRetrievingAndDistanceCalculating(ProductEntryForm.this,false,10000);
                }
                else
                {
                    LocationRetreivingGlobal llaaa=new LocationRetreivingGlobal();
                    llaaa.locationRetrievingAndDistanceCalculating(ProductEntryForm.this,false,50);
                }

            }
        }
        else if(btnClkd==5)// btn back pressed
        {

            orderBookingTotalCalc();

                progressTitle=ProductEntryForm.this.getResources().getString(R.string.WhileWeSave);

                    new SaveData().execute("5");




        }
    }


    public void orderBookingTotalCalc()
    {
        Double StandardRate=0.00;
        Double StandardRateBeforeTax=0.00;
        Double StandardTax=0.00;
        Double ActualRateAfterDiscountBeforeTax=0.00;
        Double DiscountAmount=0.00;
        Double ActualTax=0.00;
        Double ActualRateAfterDiscountAfterTax=0.00;

        String PrdMaxValuePercentageDiscount="";
        String PrdMaxValuePercentageDiscountInAmount="0";
        String PrdMaxValueFlatDiscount="";

        Double TotalFreeQTY=0.00;
        Double TotalProductLevelDiscount=0.00;
        Double TotalOrderValBeforeTax=0.00;
        Double TotAdditionaDiscount=0.00;
        Double TotOderValueAfterAdditionaDiscount=0.00;
        Double TotTaxAmount=0.00;
        Double TotOderValueAfterTax=0.00;

        LinkedHashMap<String,String> hmapPrdctOrderQty=prdctModelArrayList.getHmapPrdctOrderQty();
        if(hmapPrdctOrderQty!=null)

        for (Map.Entry<String,String> entry:hmapPrdctOrderQty.entrySet()){


            String ProductID=entry.getKey();
            String prdctQty=entry.getValue();

            if ((!TextUtils.isEmpty(prdctQty)) &&(Integer.parseInt(prdctQty)>0))
            {
                StandardRate=Double.parseDouble(hmapProductStandardRate.get(ProductID));
                StandardRateBeforeTax=StandardRate/(1+(Double.parseDouble(hmapProductVatTaxPerventage.get(ProductID))/100));

                StandardRate=Double.parseDouble(new DecimalFormat("##.##").format(StandardRate));



                     //If No Percentage Discount or Flat Discount is Applicable Code Starts Here
                        ActualRateAfterDiscountBeforeTax=StandardRateBeforeTax;
                        DiscountAmount=0.00;
                        ActualTax=ActualRateAfterDiscountBeforeTax*(Double.parseDouble(hmapProductVatTaxPerventage.get(ProductID))/100);
                        ActualRateAfterDiscountAfterTax=ActualRateAfterDiscountBeforeTax*(1+(Double.parseDouble(hmapProductVatTaxPerventage.get(ProductID))/100));

                        Double DiscAmtOnPreQtyBasic=DiscountAmount*Double.parseDouble(prdctQty);

                        Double DiscAmtOnPreQtyBasicToDisplay=DiscAmtOnPreQtyBasic;
                        DiscAmtOnPreQtyBasicToDisplay=Double.parseDouble(new DecimalFormat("##.##").format(DiscAmtOnPreQtyBasicToDisplay));



                        TotalProductLevelDiscount=TotalProductLevelDiscount+DiscAmtOnPreQtyBasic;
                        TotTaxAmount=TotTaxAmount+(ActualTax * Double.parseDouble(prdctQty));

                        Double TaxValue=ActualTax * Double.parseDouble(prdctQty);
                        TaxValue=Double.parseDouble(new DecimalFormat("##.##").format(TaxValue));
                        hmapProductTaxValue.put(ProductID, ""+TaxValue);

                        Double OrderValPrdQtyBasis=ActualRateAfterDiscountAfterTax*Double.parseDouble(prdctQty);
                        Double OrderValPrdQtyBasisToDisplay=OrderValPrdQtyBasis;
                        OrderValPrdQtyBasisToDisplay=Double.parseDouble(new DecimalFormat("##.##").format(OrderValPrdQtyBasisToDisplay));

                        hmapProductIdOrdrVal.put(ProductID, ""+OrderValPrdQtyBasis);
                        TotalOrderValBeforeTax=TotalOrderValBeforeTax+(ActualRateAfterDiscountBeforeTax*Double.parseDouble(prdctQty));
                        TotOderValueAfterTax=TotOderValueAfterTax+OrderValPrdQtyBasis;
                        //If No Percentage Discount or Flat Discount is Applicable Code Ends Here



            }

        }
        //Now the its Time to Show the OverAll Summary Code Starts Here

        tvFtotal.setText((""+ TotalFreeQTY).trim());

        TotalProductLevelDiscount=Double.parseDouble(new DecimalFormat("##.##").format(TotalProductLevelDiscount));
        tvDis.setText((""+ TotalProductLevelDiscount).trim());

        TotalOrderValBeforeTax=Double.parseDouble(new DecimalFormat("##.##").format(TotalOrderValBeforeTax));
        tv_NetInvValue.setText((""+ TotalOrderValBeforeTax).trim());

        String percentBenifitMax=dbengine.fnctnGetMaxAssignedBen8DscntApld1(storeID,strGlobalOrderID);
        Double percentMax=0.00;
        Double percentMaxGross=0.0;
        Double amountMaxGross=0.0;

        String amountBenfitMaxGross=dbengine.fnctnGetMaxAssignedBen9DscntApld2(storeID,strGlobalOrderID);
        String percentBenifitMaxGross=dbengine.fnctnGetMaxAssignedBen8DscntApld2(storeID,strGlobalOrderID);

        if(percentBenifitMaxGross.equals(""))
        {
            percentMaxGross=0.0;
        }
        else
        {
            percentMaxGross=Double.parseDouble(percentBenifitMaxGross.split(Pattern.quote("^"))[0]);
        }
        if(percentBenifitMax.equals("") )
        {
            percentMax=0.00;
        }
        else
        {
            percentMax=Double.parseDouble(percentBenifitMax.split(Pattern.quote("^"))[0]);
        }

        String amountBenifitMax=dbengine.fnctnGetMaxAssignedBen9DscntApld1(storeID,strGlobalOrderID);
        Double amountMax=0.00;
        if(percentBenifitMax.equals(""))
        {
            amountMax=0.0;
        }
        else
        {
            amountMax=Double.parseDouble(amountBenifitMax.split(Pattern.quote("^"))[0]);
        }


        tvAddDisc.setText(""+ "0.00");

        tv_NetInvAfterDiscount.setText(""+ TotalOrderValBeforeTax);

        TotTaxAmount=Double.parseDouble(new DecimalFormat("##.##").format(TotTaxAmount));
        tvTAmt.setText(""+ TotTaxAmount);

        Double totalGrossVALMaxPercentage=TotalOrderValBeforeTax-TotalOrderValBeforeTax*(percentMaxGross/100);
        Double totalGrossrVALMaxAmount=TotalOrderValBeforeTax-amountMaxGross;
        Double totalGrossVALAfterDiscount = 0.0;
        if(totalGrossVALMaxPercentage!=totalGrossrVALMaxAmount)
        {
            totalGrossVALAfterDiscount=Math.min(totalGrossrVALMaxAmount, totalGrossVALMaxPercentage);
        }
        else
        {
            totalGrossVALAfterDiscount=totalGrossrVALMaxAmount;
        }

        if(totalGrossVALAfterDiscount==totalGrossrVALMaxAmount && totalGrossrVALMaxAmount!=0.0)
        {
            dbengine.updatewhatAppliedFlag(1, storeID, Integer.parseInt(amountBenfitMaxGross.split(Pattern.quote("^"))[1]),strGlobalOrderID);
        }
        else if(totalGrossVALAfterDiscount==totalGrossVALMaxPercentage && percentMaxGross!=0.0)
        {
            dbengine.updatewhatAppliedFlag(1, storeID, Integer.parseInt(percentBenifitMaxGross.split(Pattern.quote("^"))[1]),strGlobalOrderID);
        }

        Double GrossInvValue=totalGrossVALAfterDiscount + TotTaxAmount;
        GrossInvValue=Double.parseDouble(new DecimalFormat("##.##").format(GrossInvValue));
        tv_GrossInvVal.setText(""+GrossInvValue);
        //Now the its Time to Show the OverAll Summary Code Starts Here
    }

    @Override
    public void testFunctionOne(String fnLati, String fnLongi, String finalAccuracy, String fnAccurateProvider, String GpsLat, String GpsLong, String GpsAccuracy, String NetwLat, String NetwLong, String NetwAccuracy, String FusedLat, String FusedLong, String FusedAccuracy, String AllProvidersLocation, String GpsAddress, String NetwAddress, String FusedAddress, String FusedLocationLatitudeWithFirstAttempt, String FusedLocationLongitudeWithFirstAttempt, String FusedLocationAccuracyWithFirstAttempt, int flgLocationServicesOnOff, int flgGPSOnOff, int flgNetworkOnOff, int flgFusedOnOff, int flgInternetOnOffWhileLocationTracking, String address, String pincode, String city, String state) {
        this.fnLati=fnLati;
        this.fnLongi=fnLongi;
        this.fnAccuracy=finalAccuracy;
        this.fnAccurateProvider=fnAccurateProvider;
        this.flgLocationServicesOnOffOrderReview=flgLocationServicesOnOff;
        this.flgGPSOnOffOrderReview=flgGPSOnOff;
        this.flgNetworkOnOffOrderReview=flgNetworkOnOff;
        this.flgFusedOnOffOrderReview=flgFusedOnOff;
        this.flgInternetOnOffWhileLocationTrackingOrderReview=flgInternetOnOffWhileLocationTracking;
        if(!checkLastFinalLoctionIsRepeated(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(fnAccuracy)))
        {

            fnCreateLastKnownFinalLocation(String.valueOf(fnLati), String.valueOf(fnLongi), String.valueOf(fnAccuracy));
            UpdateLocationAndProductAllData();
        }
        else
        {
            countSubmitClicked++;
            if(countSubmitClicked==1)
            {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ProductEntryForm.this);

                // Setting Dialog Title
                alertDialog.setTitle(getText(R.string.genTermNoDataConnection));
                alertDialog.setIcon(R.drawable.error_info_ico);
                alertDialog.setCancelable(false);
                // Setting Dialog Message
                alertDialog.setMessage(ProductEntryForm.this.getResources().getString(R.string.AlertSameLoc));

                // On pressing Settings button
                alertDialog.setPositiveButton(ProductEntryForm.this.getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        countSubmitClicked++;
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });

                // Showing Alert Message
                alertDialog.show();



            }
            else
            {
                UpdateLocationAndProductAllData();
            }


        }
    }

    public class SaveData extends AsyncTask<String, String, Void>
    {
        int btnClkd=-1;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Text need to e changed according to btn Click



                mProgressDialog = new ProgressDialog(ProductEntryForm.this);
                mProgressDialog.setTitle(ProductEntryForm.this.getResources().getString(R.string.Loading));
                mProgressDialog.setMessage(progressTitle);
                mProgressDialog.setIndeterminate(true);
                mProgressDialog.setCancelable(false);
                mProgressDialog.show();

        }

        @Override
        protected Void doInBackground(String... params)
        {
            String executedData=params[0];



            btnClkd=Integer.parseInt(executedData);


            fnSaveFilledDataToDatabase(btnClkd);
            return null;
        }
        @Override
        protected void onPostExecute(Void args) {

            if(mProgressDialog.isShowing()==true)
            {
                mProgressDialog.dismiss();
            }
            long syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat(
                    "dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
            String startTS = df.format(dateobj);
            dbengine.open();
            dbengine.UpdateStoreEndVisit(storeID,startTS);
            dbengine.close();
            if(btnClkd==6)
            {
                Intent storeOrderReviewIntent = new Intent(ProductEntryForm.this, OrderReview.class);
                storeOrderReviewIntent.putExtra("storeID", storeID);
                storeOrderReviewIntent.putExtra("SN", SN);
                storeOrderReviewIntent.putExtra("bck", 1);
                storeOrderReviewIntent.putExtra("imei", imei);
                storeOrderReviewIntent.putExtra("userdate", date);
                storeOrderReviewIntent.putExtra("pickerDate", pickerDate);
                storeOrderReviewIntent.putExtra("flgOrderType", flgOrderType);
                //fireBackDetPg.putExtra("rID", routeID);
                startActivity(storeOrderReviewIntent);
                finish();
               /* Intent fireBackDetPg=new Intent(ProductEntryForm.this,ReturnActivity.class);
                fireBackDetPg.putExtra("storeID", storeID);
                fireBackDetPg.putExtra("SN", SN);
                fireBackDetPg.putExtra("bck", 1);
                fireBackDetPg.putExtra("imei", imei);
                fireBackDetPg.putExtra("userdate", date);
                fireBackDetPg.putExtra("pickerDate", pickerDate);
                fireBackDetPg.putExtra("OrderPDAID", strGlobalOrderID);
                fireBackDetPg.putExtra("flgPageToRedirect", "1");
                fireBackDetPg.putExtra("flgOrderType", flgOrderType);
                // fireBackDetPg.putExtra("rID", routeID);

                startActivity(fireBackDetPg);
                finish();*/
            }

            else if(btnClkd==2)
            {
                Intent storeSaveIntent = new Intent(ProductEntryForm.this, LauncherActivity.class);
                startActivity(storeSaveIntent);
                finish();
            }
            else if(btnClkd==5)
            {
                //Intent fireBackDetPg=new Intent(ProductOrderSearch.this,POSMaterialActivity.class);
                if(flgOrderType==1)
                {

                    Intent fireBackDetPg=new Intent(ProductEntryForm.this,LastVisitDetails.class);
                    fireBackDetPg.putExtra("storeID", storeID);
                    fireBackDetPg.putExtra("SN", SN);
                    fireBackDetPg.putExtra("bck", 1);
                    fireBackDetPg.putExtra("imei", imei);
                    fireBackDetPg.putExtra("userdate", date);
                    fireBackDetPg.putExtra("pickerDate", pickerDate);
                    //fireBackDetPg.putExtra("rID", routeID);
                    startActivity(fireBackDetPg);
                    finish();

                }
                else
                {
                    Intent prevP2 = new Intent(ProductEntryForm.this, StoreSelection.class);
                    String routeID=dbengine.GetActiveRouteIDSunil();
                    //Location_Getting_Service.closeFlag = 0;
                    prevP2.putExtra("imei", imei);
                    prevP2.putExtra("userDate", date);
                    prevP2.putExtra("pickerDate", pickerDate);
                    prevP2.putExtra("rID", routeID);
                    startActivity(prevP2);
                    finish();

                }

            }



        }


    }

    public void fnSaveFilledDataToDatabase(int valBtnClickedFrom)
    {

           if(valBtnClickedFrom==1)//Clicked By Btn Save
        {
            //Change Ostat Val=2
            int Outstat=1;
            TransactionTableDataDeleteAndSaving(Outstat);
            InvoiceTableDataDeleteAndSaving(Outstat);
            dbengine.open();
            dbengine.UpdateStoreFlag(storeID.trim(), 1);
            dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 1,strGlobalOrderID);
            dbengine.UpdateStoreStoreReturnDetail(storeID.trim(),"1",strGlobalOrderID);
            dbengine.UpdateStoreProductAppliedSchemesBenifitsRecords(storeID.trim(),"1",strGlobalOrderID);


            long  syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
            String StampEndsTime = df.format(dateobj);


            dbengine.UpdateStoreEndVisit(storeID, StampEndsTime);
            dbengine.close();
            dbengine.updateStoreQuoteSubmitFlgInStoreMstr(storeID.trim(),0);
            if(dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID)==0)
            {
                String strDefaultPaymentStageForStore=dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                if(!strDefaultPaymentStageForStore.equals(""))
                {
                    dbengine.open();
                    dbengine. fnsaveStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID,strDefaultPaymentStageForStore,"1");
                    dbengine.close();
                }
            }
        }
        if(valBtnClickedFrom==2)//Clicked By Btn Save and Exit
        {
            //Go to Store List Page
            //Change Ostat Val=2

            //change by Sunil
            int Outstat=1;
            TransactionTableDataDeleteAndSaving(Outstat);
            InvoiceTableDataDeleteAndSaving(Outstat);
            dbengine.open();
            dbengine.UpdateStoreFlag(storeID.trim(), 1);
            dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 1,strGlobalOrderID);
            dbengine.UpdateStoreStoreReturnDetail(storeID.trim(),"1",strGlobalOrderID);
            dbengine.UpdateStoreProductAppliedSchemesBenifitsRecords(storeID.trim(),"1",strGlobalOrderID);

            long  syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
            String StampEndsTime = df.format(dateobj);


            dbengine.UpdateStoreEndVisit(storeID, StampEndsTime);
            dbengine.close();
            if(dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID)==0)
            {
                String strDefaultPaymentStageForStore=dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                if(!strDefaultPaymentStageForStore.equals(""))
                {
                    dbengine.open();
                    dbengine. fnsaveStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID,strDefaultPaymentStageForStore,"1");
                    dbengine.close();
                }
            }


        }

        if(valBtnClickedFrom==6)//Clicked By Btn Save and Exit
        {
            //Go to Store List Page
            //Change Ostat Val=2

            //change by Sunil
            int Outstat=1;
            TransactionTableDataDeleteAndSaving(Outstat);
            InvoiceTableDataDeleteAndSaving(Outstat);
            dbengine.open();
            dbengine.UpdateStoreFlag(storeID.trim(), 1);
            dbengine.UpdateStoreOtherMainTablesFlag(storeID.trim(), 1,strGlobalOrderID);
            dbengine.UpdateStoreStoreReturnDetail(storeID.trim(),"1",strGlobalOrderID);
            dbengine.UpdateStoreProductAppliedSchemesBenifitsRecords(storeID.trim(),"1",strGlobalOrderID);
            dbengine.close();

            if(dbengine.checkCountIntblStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID)==0)
            {
                String strDefaultPaymentStageForStore=dbengine.fnGetDefaultStoreOrderPAymentDetails(storeID);
                if(!strDefaultPaymentStageForStore.equals(""))
                {
                    dbengine.open();
                    dbengine. fnsaveStoreSalesOrderPaymentDetails(storeID,strGlobalOrderID,strDefaultPaymentStageForStore,"1");
                    dbengine.close();
                }
            }
            long  syncTIMESTAMP = System.currentTimeMillis();
            Date dateobj = new Date(syncTIMESTAMP);
            SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss",Locale.ENGLISH);
            String StampEndsTime = df.format(dateobj);








        }

    }

    public void TransactionTableDataDeleteAndSaving(int Outstat)
    {

        dbengine.deleteStoreRecordFromtblStorePurchaseDetailsFromProductTrsaction(storeID,strGlobalOrderID);

        LinkedHashMap<String,String> hmapPrdctOrderQty=prdctModelArrayList.getHmapPrdctOrderQty();
        if(hmapPrdctOrderQty!=null)
        {
            for (Map.Entry<String, String> entry:hmapPrdctOrderQty.entrySet() )
            {
                //  View vRow = ll_prdct_detal.getChildAt(index);

                int PCateId=Integer.parseInt(hmapCtgryPrdctDetail.get(entry.getKey()));
                String PName =entry.getValue();
                String ProductID=entry.getKey();
                String ProductStock ="0";
                if(hmapProductIdStock!=null && hmapProductIdStock.containsKey(ProductID))
                {
                    ProductStock= hmapProductIdStock.get(ProductID);
                }
                double TaxRate=0.00;
                double TaxValue=0.00;
                if(ProductStock.equals(""))
                {
                    ProductStock="0";
                }
                String SampleQTY ="";
                if(SampleQTY.equals(""))
                {
                    SampleQTY="0";
                }
                String OrderQTY =entry.getValue();

                if(TextUtils.isEmpty(OrderQTY))
                {
                    OrderQTY="0";

                }
                String OrderValue="0";
                if(Integer.parseInt(OrderQTY)>0)
                {
                    OrderValue =hmapProductIdOrdrVal.get(ProductID);// ((TextView)(vRow).findViewById(R.id.tv_Orderval)).getText().toString();
                    if(OrderValue.equals(""))
                    {
                        OrderValue="0";
                    }
                }

                String OrderFreeQty ="0";


                String OrderDisVal= "0";

                String PRate=hmapProductStandardRate.get(ProductID);//.split(Pattern.quote("^"))[1];
                TaxRate=Double.parseDouble(hmapProductVatTaxPerventage.get(ProductID));
                TaxValue=Double.parseDouble(hmapProductTaxValue.get(ProductID));
                int flgIsQuoteRateApplied=0;


                // String TransDate=date;
                if(Integer.valueOf(OrderFreeQty)>0 || Integer.valueOf(SampleQTY)>0 || Integer.valueOf(OrderQTY)>0 || Integer.valueOf(OrderValue)>0 || Integer.valueOf(OrderDisVal)>0 || Integer.valueOf(ProductStock)>0)
                {
                    dbengine.open();
                    StoreCatNodeId=dbengine.fnGetStoreCatNodeId(storeID);
                    dbengine.fnsaveStoreProdcutPurchaseDetails(imei,storeID,""+PCateId,ProductID,pickerDate,Integer.parseInt(ProductStock),Integer.parseInt(OrderQTY),Double.parseDouble(OrderValue),Integer.parseInt(OrderFreeQty.split(Pattern.quote("."))[0]),Double.parseDouble(OrderDisVal),Integer.parseInt(SampleQTY),PName,Double.parseDouble(PRate),Outstat,TaxRate,TaxValue,StoreCatNodeId,strGlobalOrderID,flgIsQuoteRateApplied,distID,flgOrderType);
                    dbengine.close();
                }



            }
        }



    }

    public void InvoiceTableDataDeleteAndSaving(int Outstat)
    {

        dbengine.deleteOldStoreInvoice(storeID,strGlobalOrderID);

        Double TBtaxDis;
        Double TAmt;
        Double Dis;
        Double INval;

        Double AddDis;
        Double InvAfterDis;

        Double INvalCreditAmt;
        Double INvalInvoiceAfterCreditAmt;

        Double INvalInvoiceOrginal=0.00;


        int Ftotal;




        if(!tv_NetInvValue.getText().toString().isEmpty()){
            TBtaxDis = Double.parseDouble(tv_NetInvValue.getText().toString().trim());
        }
        else{
            TBtaxDis = 0.00;
        }
        if(!tvTAmt.getText().toString().isEmpty()){
            TAmt = Double.parseDouble(tvTAmt.getText().toString().trim());
        }
        else{
            TAmt = 0.00;
        }
        if(!tvDis.getText().toString().isEmpty()){
            Dis = Double.parseDouble(tvDis.getText().toString().trim());
        }
        else{
            Dis = 0.00;
        }
        if(!tv_GrossInvVal.getText().toString().isEmpty()){

		/*	if(Dis!=0.00)
			{
				INval = Double.parseDouble(tvINval.getText().toString().trim())-Dis;
			}
			else
			{
				INval = Double.parseDouble(tvINval.getText().toString().trim());
			}*/
            INval = Double.parseDouble(tv_GrossInvVal.getText().toString().trim());
        }
        else{
            INval = 0.00;
        }
        if(!tvFtotal.getText().toString().isEmpty()){
            Double FtotalValue=Double.parseDouble(tvFtotal.getText().toString().trim());
            Ftotal =FtotalValue.intValue();
        }
        else{
            Ftotal = 0;
        }

        if(!tv_NetInvAfterDiscount.getText().toString().isEmpty()){
            InvAfterDis = Double.parseDouble(tv_NetInvAfterDiscount.getText().toString().trim());
        }
        else{
            InvAfterDis = 0.00;
        }
        if(!tvAddDisc.getText().toString().isEmpty()){
            AddDis = Double.parseDouble(tvAddDisc.getText().toString().trim());
        }
        else{
            AddDis = 0.00;
        }


        Double AmtPrevDueVA=0.00;
        Double AmtCollVA=0.00;
        Double AmtOutstandingVAL=0.00;
        if(!tvAmtPrevDueVAL.getText().toString().isEmpty()){
            AmtPrevDueVA = Double.parseDouble(tvAmtPrevDueVAL.getText().toString().trim());
        }
        else{
            AmtPrevDueVA = 0.00;
        }
        if(!etAmtCollVAL.getText().toString().isEmpty()){
            AmtCollVA = Double.parseDouble(etAmtCollVAL.getText().toString().trim());
        }
        else{
            AmtCollVA = 0.00;
        }

        if(!tvAmtOutstandingVAL.getText().toString().isEmpty()){
            AmtOutstandingVAL = Double.parseDouble(tvAmtOutstandingVAL.getText().toString().trim());
        }
        else{
            AmtOutstandingVAL = 0.00;
        }

        int NoOfCouponValue=0;
		/*if(!txttvNoOfCouponValue.getText().toString().isEmpty()){
			NoOfCouponValue = Integer.parseInt(txttvNoOfCouponValue.getText().toString().trim());
		}
		else{
			NoOfCouponValue = 0;
		}
		*/
        Double TotalCoupunAmount=0.00;
        if(!txttvCouponAmountValue.getText().toString().isEmpty()){
            TotalCoupunAmount = Double.parseDouble(txttvCouponAmountValue.getText().toString().trim());
        }
        else{
            TotalCoupunAmount = 0.00;
        }


        dbengine.open();
        dbengine.saveStoreInvoice(imei,storeID, pickerDate, TBtaxDis, TAmt, Dis, INval, Ftotal, InvAfterDis, AddDis, AmtPrevDueVA, AmtCollVA, AmtOutstandingVAL, NoOfCouponValue, TotalCoupunAmount,Outstat,strGlobalOrderID);//, INvalCreditAmt, INvalInvoiceAfterCreditAmt, valInvoiceOrginal);
        dbengine.close();



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!isSettingAlertOpen)
        {
            locationManager=(LocationManager) this.getSystemService(LOCATION_SERVICE);

            boolean isGPSok = false;
            boolean isNWok=false;
            isGPSok = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNWok = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if(!isGPSok)
            {
                isGPSok = false;
            }
            if(!isNWok)
            {
                isNWok = false;
            }
            if(!isGPSok && !isNWok)
            {
                try
                {
                    showSettingsAlert();
                }
                catch(Exception e)
                {

                }

                isGPSok = false;
                isNWok=false;
            }
        }

    }


    public void showSettingsAlert()
    {
        isSettingAlertOpen=true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle(getText(R.string.genTermNoDataConnection));
        alertDialog.setIcon(R.drawable.error_info_ico);
        alertDialog.setCancelable(false);
        // Setting Dialog Message
        alertDialog.setMessage(ProductEntryForm.this.getResources().getString(R.string.genTermGPSDisablePleaseEnable));

        // On pressing Settings button
        alertDialog.setPositiveButton(ProductEntryForm.this.getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                isSettingAlertOpen=false;
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    public void UpdateLocationAndProductAllData()
    {
        checkHighAccuracyLocationMode(ProductEntryForm.this);
        dbengine.open();
        dbengine.UpdateStoreActualLatLongi(storeID,String.valueOf(fnLati), String.valueOf(fnLongi), "" + fnAccuracy,fnAccurateProvider,flgLocationServicesOnOffOrderReview,flgGPSOnOffOrderReview,flgNetworkOnOffOrderReview,flgFusedOnOffOrderReview,flgInternetOnOffWhileLocationTrackingOrderReview,flgRestartOrderReview,flgStoreOrderOrderReview);


        dbengine.close();





                progressTitle=ProductEntryForm.this.getResources().getString(R.string.WhileWeSave);
                new SaveData().execute(butnClkd);








    }

    public void checkHighAccuracyLocationMode(Context context) {
        int locationMode = 0;
        String locationProviders;

        flgLocationServicesOnOffOrderReview=0;
        flgGPSOnOffOrderReview=0;
        flgNetworkOnOffOrderReview=0;
        flgFusedOnOffOrderReview=0;
        flgInternetOnOffWhileLocationTrackingOrderReview=0;

        if(isGooglePlayServicesAvailable())
        {
            flgFusedOnOffOrderReview=1;
        }
        if(isOnline())
        {
            flgInternetOnOffWhileLocationTrackingOrderReview=1;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            //Equal or higher than API 19/KitKat
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
                if (locationMode == Settings.Secure.LOCATION_MODE_HIGH_ACCURACY){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=1;
                    flgNetworkOnOffOrderReview=1;
                    //flgFusedOnOff=1;
                }
                if (locationMode == Settings.Secure.LOCATION_MODE_BATTERY_SAVING){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=0;
                    flgNetworkOnOffOrderReview=1;
                    // flgFusedOnOff=1;
                }
                if (locationMode == Settings.Secure.LOCATION_MODE_SENSORS_ONLY){
                    flgLocationServicesOnOffOrderReview=1;
                    flgGPSOnOffOrderReview=1;
                    flgNetworkOnOffOrderReview=0;
                    //flgFusedOnOff=0;
                }
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
            }
        }
        else {
            //Lower than API 19
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);


            if (TextUtils.isEmpty(locationProviders)) {
                locationMode = Settings.Secure.LOCATION_MODE_OFF;

                flgLocationServicesOnOffOrderReview = 0;
                flgGPSOnOffOrderReview = 0;
                flgNetworkOnOffOrderReview = 0;
                // flgFusedOnOff = 0;
            }
            if (locationProviders.contains(LocationManager.GPS_PROVIDER) && locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                flgLocationServicesOnOffOrderReview = 1;
                flgGPSOnOffOrderReview = 1;
                flgNetworkOnOffOrderReview = 1;
                //flgFusedOnOff = 0;
            } else {
                if (locationProviders.contains(LocationManager.GPS_PROVIDER)) {
                    flgLocationServicesOnOffOrderReview = 1;
                    flgGPSOnOffOrderReview = 1;
                    flgNetworkOnOffOrderReview = 0;
                    // flgFusedOnOff = 0;
                }
                if (locationProviders.contains(LocationManager.NETWORK_PROVIDER)) {
                    flgLocationServicesOnOffOrderReview = 1;
                    flgGPSOnOffOrderReview = 0;
                    flgNetworkOnOffOrderReview = 1;
                    //flgFusedOnOff = 0;
                }
            }
        }

    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }


    public boolean checkLastFinalLoctionIsRepeated(String currentLat,String currentLong,String currentAccuracy){
        boolean repeatedLoction=false;

        try {

            String chekLastGPSLat="0";
            String chekLastGPSLong="0";
            String chekLastGpsAccuracy="0";
            File jsonTxtFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.FinalLatLngJsonFile);
            if (!jsonTxtFolder.exists())
            {
                jsonTxtFolder.mkdirs();

            }
            String txtFileNamenew="FinalGPSLastLocation.txt";
            File file = new File(jsonTxtFolder,txtFileNamenew);
            String fpath = Environment.getExternalStorageDirectory()+"/"+CommonInfo.FinalLatLngJsonFile+"/"+txtFileNamenew;

            // If file does not exists, then create it
            if (file.exists()) {
                StringBuffer buffer=new StringBuffer();
                String myjson_stampiGPSLastLocation="";
                StringBuffer sb = new StringBuffer();
                BufferedReader br = null;

                try {
                    br = new BufferedReader(new FileReader(file));

                    String temp;
                    while ((temp = br.readLine()) != null)
                        sb.append(temp);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close(); // stop reading
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                myjson_stampiGPSLastLocation=sb.toString();

                JSONObject jsonObjGPSLast = new JSONObject(myjson_stampiGPSLastLocation);
                JSONArray jsonObjGPSLastInneralues = jsonObjGPSLast.getJSONArray("GPSLastLocationDetils");

                String StringjsonGPSLastnew = jsonObjGPSLastInneralues.getString(0);
                JSONObject jsonObjGPSLastnewwewe = new JSONObject(StringjsonGPSLastnew);

                chekLastGPSLat=jsonObjGPSLastnewwewe.getString("chekLastGPSLat");
                chekLastGPSLong=jsonObjGPSLastnewwewe.getString("chekLastGPSLong");
                chekLastGpsAccuracy=jsonObjGPSLastnewwewe.getString("chekLastGpsAccuracy");

                if(currentLat!=null )
                {
                    if(currentLat.equals(chekLastGPSLat) && currentLong.equals(chekLastGPSLong) && currentAccuracy.equals(chekLastGpsAccuracy))
                    {
                        repeatedLoction=true;
                    }
                }
            }
        }
        catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return repeatedLoction;

    }

    public void fnCreateLastKnownFinalLocation(String chekLastGPSLat,String chekLastGPSLong,String chekLastGpsAccuracy)
    {

        try {

            JSONArray jArray=new JSONArray();
            JSONObject jsonObjMain=new JSONObject();


            JSONObject jOnew = new JSONObject();
            jOnew.put( "chekLastGPSLat",chekLastGPSLat);
            jOnew.put( "chekLastGPSLong",chekLastGPSLong);
            jOnew.put( "chekLastGpsAccuracy", chekLastGpsAccuracy);


            jArray.put(jOnew);
            jsonObjMain.put("GPSLastLocationDetils", jArray);

            File jsonTxtFolder = new File(Environment.getExternalStorageDirectory(), CommonInfo.FinalLatLngJsonFile);
            if (!jsonTxtFolder.exists())
            {
                jsonTxtFolder.mkdirs();

            }
            String txtFileNamenew="FinalGPSLastLocation.txt";
            File file = new File(jsonTxtFolder,txtFileNamenew);
            String fpath = Environment.getExternalStorageDirectory()+"/"+CommonInfo.FinalLatLngJsonFile+"/"+txtFileNamenew;


            // If file does not exists, then create it
            if (!file.exists()) {
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }


            FileWriter fw;
            try {
                fw = new FileWriter(file.getAbsoluteFile());

                BufferedWriter bw = new BufferedWriter(fw);

                bw.write(jsonObjMain.toString());

                bw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
				 /*  file=contextcopy.getFilesDir();
				//fileOutputStream=contextcopy.openFileOutput("FinalGPSLastLocation.txt", Context.MODE_PRIVATE);
				fileOutputStream.write(jsonObjMain.toString().getBytes());
				fileOutputStream.close();*/
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally{

        }
    }


    public void executionData()
    {

        LayoutInflater layoutInflater = LayoutInflater.from(ProductEntryForm.this);
        View promptView = layoutInflater.inflate(R.layout.lastsummary_execution, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProductEntryForm.this);


        alertDialogBuilder.setTitle(ProductEntryForm.this.getResources().getString(R.string.genTermInformation));



        dbengine.open();

        String DateResult[]=dbengine.fetchOrderDateFromtblForPDAGetExecutionSummary(storeID);
        String LastexecutionDetail[]=dbengine.fetchAllDataFromtbltblForPDAGetExecutionSummary(storeID);

        String PrdNameDetail[]=dbengine.fetchPrdNameFromtblForPDAGetExecutionSummary(storeID);

        String ProductIDDetail[]=dbengine.fetchProductIDFromtblForPDAGetExecutionSummary(storeID);


        //System.out.println("Ashish and Anuj LastexecutionDetail : "+LastexecutionDetail.length);
        dbengine.close();

        if(DateResult.length>0)
        {
            TextView FirstDate = (TextView)promptView.findViewById(R.id.FirstDate);
            TextView SecondDate = (TextView)promptView.findViewById(R.id.SecondDate);
            TextView ThirdDate = (TextView)promptView.findViewById(R.id.ThirdDate);

            TextView lastExecution = (TextView)promptView.findViewById(R.id.lastExecution);
            lastExecution.setText(ProductEntryForm.this.getResources().getString(R.string.lastvisitdetails_last)
                    +DateResult.length+ProductEntryForm.this.getResources().getString(R.string.ExecSummary));





            if(DateResult.length==1)
            {
                FirstDate.setText(""+DateResult[0]);
                SecondDate.setVisibility(View.GONE);
                ThirdDate.setVisibility(View.GONE);
            }
            else if(DateResult.length==2)
            {
                FirstDate.setText(""+DateResult[0]);
                SecondDate.setText(""+DateResult[1]);
                ThirdDate.setVisibility(View.GONE);
            }
            else if(DateResult.length==3)
            {
                FirstDate.setText(""+DateResult[0]);
                SecondDate.setText(""+DateResult[1]);
                ThirdDate.setText(""+DateResult[2]);
            }
        }

        LayoutInflater inflater = getLayoutInflater();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        double x = Math.pow(dm.widthPixels/dm.xdpi,2);
        double y = Math.pow(dm.heightPixels/dm.ydpi,2);
        double screenInches = Math.sqrt(x+y);
        if(LastexecutionDetail.length>0)
        {
            alertDialogBuilder.setView(promptView);




            TableLayout tbl1_dyntable_For_ExecutionDetails = (TableLayout) promptView.findViewById(R.id.dyntable_For_ExecutionDetails);
            TableRow row1 = (TableRow)inflater.inflate(R.layout.table_execution_head, tbl1_dyntable_For_OrderDetails, false);

            TextView firstDateOrder = (TextView)row1.findViewById(R.id.firstDateOrder);
            TextView firstDateInvoice = (TextView)row1.findViewById(R.id.firstDateInvoice);
            TextView secondDateOrder = (TextView)row1.findViewById(R.id.secondDateOrder);
            TextView secondDateInvoice = (TextView)row1.findViewById(R.id.secondDateInvoice);
            TextView thirdDateOrder = (TextView)row1.findViewById(R.id.thirdDateOrder);
            TextView thirdDateInvoice = (TextView)row1.findViewById(R.id.thirdDateInvoice);
            if(DateResult.length>0)
            {
                if(DateResult.length==1)
                {

                    secondDateOrder.setVisibility(View.GONE);
                    secondDateInvoice.setVisibility(View.GONE);
                    thirdDateOrder.setVisibility(View.GONE);
                    thirdDateInvoice.setVisibility(View.GONE);
                }
                else if(DateResult.length==2)
                {
                    thirdDateOrder.setVisibility(View.GONE);
                    thirdDateInvoice.setVisibility(View.GONE);
                }
            }

            tbl1_dyntable_For_ExecutionDetails.addView(row1);


            for (int current = 0; current <= (PrdNameDetail.length - 1); current++)
            {


                final TableRow row = (TableRow)inflater.inflate(R.layout.table_execution_row, tbl1_dyntable_For_OrderDetails, false);

                TextView tv1 = (TextView)row.findViewById(R.id.skuName);
                TextView tv2 = (TextView)row.findViewById(R.id.firstDateOrder);
                TextView tv3 = (TextView)row.findViewById(R.id.firstDateInvoice);
                TextView tv4 = (TextView)row.findViewById(R.id.secondDateOrder);
                TextView tv5 = (TextView)row.findViewById(R.id.secondDateInvoice);
                TextView tv6 = (TextView)row.findViewById(R.id.thirdDateOrder);
                TextView tv7 = (TextView)row.findViewById(R.id.thirdDateInvoice);

                tv1.setText(PrdNameDetail[current]);

                if(DateResult.length>0)
                {
                    if(DateResult.length==1)
                    {
                        tv4.setVisibility(View.GONE);
                        tv5.setVisibility(View.GONE);
                        tv6.setVisibility(View.GONE);
                        tv7.setVisibility(View.GONE);
                        dbengine.open();
                        String abc[]=dbengine.fetchAllDataNewFromtbltblForPDAGetExecutionSummary(storeID,DateResult[0],ProductIDDetail[current]);
                        dbengine.close();

                        //System.out.println("Check Value Number "+abc.length);
                        //System.out.println("Check Value Number12 "+DateResult[0]);
                        if(abc.length>0)
                        {
                            StringTokenizer tokens = new StringTokenizer(String.valueOf(abc[0]), "_");
                            tv2.setText(tokens.nextToken().trim());
                            tv3.setText(tokens.nextToken().trim());
                        }
                        else
                        {
                            tv2.setText("0");
                            tv3.setText("0");
                        }
                    }
                    else if(DateResult.length==2)
                    {
                        tv6.setVisibility(View.GONE);
                        tv7.setVisibility(View.GONE);

                        dbengine.open();
                        String abc[]=dbengine.fetchAllDataNewFromtbltblForPDAGetExecutionSummary(storeID,DateResult[0],ProductIDDetail[current]);
                        dbengine.close();

                        //System.out.println("Check Value Number "+abc.length);
                        //System.out.println("Check Value Number12 "+DateResult[0]);
                        if(abc.length>0)
                        {
                            StringTokenizer tokens = new StringTokenizer(String.valueOf(abc[0]), "_");
                            tv2.setText(tokens.nextToken().trim());
                            tv3.setText(tokens.nextToken().trim());
                        }
                        else
                        {
                            tv2.setText("0");
                            tv3.setText("0");
                        }

                        dbengine.open();
                        String abc1[]=dbengine.fetchAllDataNewFromtbltblForPDAGetExecutionSummary(storeID,DateResult[1],ProductIDDetail[current]);
                        dbengine.close();


                        if(abc1.length>0)
                        {
                            StringTokenizer tokens = new StringTokenizer(String.valueOf(abc1[0]), "_");
                            tv4.setText(tokens.nextToken().trim());
                            tv5.setText(tokens.nextToken().trim());
                        }
                        else
                        {
                            tv4.setText("0");
                            tv5.setText("0");
                        }





                    }
                    else if(DateResult.length==3)
                    {
                        dbengine.open();
                        String abc[]=dbengine.fetchAllDataNewFromtbltblForPDAGetExecutionSummary(storeID,DateResult[0],ProductIDDetail[current]);
                        dbengine.close();


                        if(abc.length>0)
                        {
                            StringTokenizer tokens = new StringTokenizer(String.valueOf(abc[0]), "_");
                            tv2.setText(tokens.nextToken().trim());
                            tv3.setText(tokens.nextToken().trim());
                        }
                        else
                        {
                            tv2.setText("0");
                            tv3.setText("0");
                        }

                        dbengine.open();
                        String abc1[]=dbengine.fetchAllDataNewFromtbltblForPDAGetExecutionSummary(storeID,DateResult[1],ProductIDDetail[current]);
                        dbengine.close();


                        if(abc1.length>0)
                        {
                            StringTokenizer tokens = new StringTokenizer(String.valueOf(abc1[0]), "_");
                            tv4.setText(tokens.nextToken().trim());
                            tv5.setText(tokens.nextToken().trim());
                        }
                        else
                        {
                            tv4.setText("0");
                            tv5.setText("0");
                        }

                        dbengine.open();
                        String abc2[]=dbengine.fetchAllDataNewFromtbltblForPDAGetExecutionSummary(storeID,DateResult[2],ProductIDDetail[current]);
                        dbengine.close();


                        if(abc2.length>0)
                        {
                            StringTokenizer tokens = new StringTokenizer(String.valueOf(abc2[0]), "_");
                            tv6.setText(tokens.nextToken().trim());
                            tv7.setText(tokens.nextToken().trim());
                        }
                        else
                        {
                            tv6.setText("0");
                            tv7.setText("0");
                        }





                    }
                    else
                    {

                    }
                }

                tbl1_dyntable_For_ExecutionDetails.addView(row);

            }

        }
        else
        {
            alertDialogBuilder.setMessage(ProductEntryForm.this.getResources().getString(R.string.AlertExecNoSum));
        }
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton(ProductEntryForm.this.getResources().getString(R.string.AlertDialogOkButton), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {}
                });


        alertDialogBuilder.setIcon(R.drawable.info_ico);
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();


    }
}
