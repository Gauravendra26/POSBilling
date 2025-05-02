package de.syss.MifareClassicTool.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.content.ContextCompat;
import androidx.core.text.HtmlCompat;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Locale;

import de.syss.MifareClassicTool.Common;
import de.syss.MifareClassicTool.ConnectionHelper;
import de.syss.MifareClassicTool.MCReader;
import de.syss.MifareClassicTool.Menu_Activity;
import de.syss.MifareClassicTool.R;
import de.syss.MifareClassicTool.Select_Location_Activity;
import de.syss.MifareClassicTool.Utils;

public class DumpEditor extends BasicActivity
    implements IActivityThatReactsToSave {

    public final static String EXTRA_DUMP =
        "de.syss.MifareClassicTool.Activity.DUMP";

    private static final String LOG_TAG =
        DumpEditor.class.getSimpleName();

    private LinearLayout mLayout;
    private String mDumpName;
    private String mKeysName;
    private String mUID;

    private String[] mLines;

    private boolean mDumpChanged;

    private boolean mCloseAfterSuccessfulSave;
    String memberID,memberType,couponNo,cashierCode,status,Flag;
    String serialno, MAINID, name, counponno, amount = "1",checkLogin="1";
    TextView tvId, tvName, tvCardAmount, tvCardHolderName, tvCard, tvInformation;
    RelativeLayout rlContinue;
    LinearLayout llupper, llCardDeatils;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dump_editor);
        Utils.blackIconStatusBar(this, R.color.main);
        init();

        // Color caption.
        SpannableString keyA = Common.colorString(
            getString(R.string.text_keya),
            ContextCompat.getColor(this, R.color.light_green));
        SpannableString keyB = Common.colorString(
            getString(R.string.text_keyb),
            ContextCompat.getColor(this, R.color.dark_green));
        SpannableString ac = Common.colorString(
            getString(R.string.text_ac),
            ContextCompat.getColor(this, R.color.orange));
        SpannableString uidAndManuf = Common.colorString(
            getString(R.string.text_uid_and_manuf),
            ContextCompat.getColor(this, R.color.purple));
        SpannableString vb = Common.colorString(
            getString(R.string.text_valueblock),
            ContextCompat.getColor(this, R.color.yellow));

        TextView caption = findViewById(
            R.id.textViewDumpEditorCaption);
        caption.setText(TextUtils.concat(uidAndManuf, " | ",
            vb, " | ", keyA, " | ", keyB, " | ", ac), BufferType.SPANNABLE);
        // Add web-link optic to update colors text view (= caption title).
        TextView captionTitle = findViewById(
            R.id.textViewDumpEditorCaptionTitle);
        SpannableString updateText = Common.colorString(
            getString(R.string.text_update_colors), Common.getThemeAccentColor(this));
        updateText.setSpan(new UnderlineSpan(), 0, updateText.length(), 0);
        captionTitle.setText(TextUtils.concat(
            getString(R.string.text_caption_title),
            ": (", updateText, ")"));

        rlContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Intent intent = new Intent(getApplicationContext(), Menu_Activity.class);
                intent.putExtra("name", name);
                intent.putExtra("memberID", memberID);
                intent.putExtra("MAINID", MAINID);
                intent.putExtra("memberType", memberType);
                intent.putExtra("couponNo", couponNo);
                intent.putExtra("cashierCode", cashierCode);
                intent.putExtra("checkOccupiedtable", "0");
                startActivity(intent);


            }
        });

        if (getIntent().hasExtra(EXTRA_DUMP)) {
            // Called from ReadTag (init editor by intent).
            String[] dump = getIntent().getStringArrayExtra(EXTRA_DUMP);
            // Set title with UID.
            if (Common.getUID() != null) {
                mUID = Common.bytes2Hex(Common.getUID());
                setTitle(getTitle() + " (UID: " + mUID + ")");
            }
            initEditor(dump);
            setIntent(null);
        } else if (getIntent().hasExtra(
            FileChooser.EXTRA_CHOSEN_FILE)) {
            // Called form FileChooser (init editor by file).
            File file = new File(getIntent().getStringExtra(
                FileChooser.EXTRA_CHOSEN_FILE));
            mDumpName = file.getName();
            setTitle(getTitle() + " (" + mDumpName + ")");
            initEditor(Common.readFileLineByLine(file, false, this));
            setIntent(null);
        } else if (savedInstanceState != null) {
            // Recreated after kill by Android (due to low memory).
            mCloseAfterSuccessfulSave = savedInstanceState.getBoolean(
                "close_after_successful_save");
            mDumpChanged = savedInstanceState.getBoolean("dump_changed");
            mKeysName = savedInstanceState.getString("keys_name");
            mUID = savedInstanceState.getString("uid");
            if (mUID != null) {
                setTitle(getTitle() + " (" + mUID + ")");
            }
            mDumpName = savedInstanceState.getString("dump_name");
            if (mDumpName != null) {
                setTitle(getTitle() + " (" + mDumpName + ")");
            }
            mLines = savedInstanceState.getStringArray("lines");
            if (mLines != null) {
                initEditor(mLines);
            }
        }
    }

    void init() {
        mLayout = findViewById(R.id.linearLayoutDumpEditor);
        tvId = findViewById(R.id.tvId);
        tvName = findViewById(R.id.tvName);
        tvCardAmount = findViewById(R.id.tvCardAmount);
        tvCardHolderName = findViewById(R.id.tvCardHolderName);
        tvCard = findViewById(R.id.tvCard);
        tvInformation = findViewById(R.id.tvInformation);
        rlContinue = findViewById(R.id.rlContinue);
        llupper = findViewById(R.id.llupper);
        llCardDeatils = findViewById(R.id.llCardDeatils);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean("dump_changed", mDumpChanged);
        outState.putBoolean("close_after_successful_save", mCloseAfterSuccessfulSave);
        outState.putString("keys_name", mKeysName);
        outState.putString("dump_name", mDumpName);
        outState.putString("uid", mUID);
        outState.putStringArray("lines", mLines);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dump_editor_functions, menu);
        // Enable/Disable write dump function depending on NFC availability.
        menu.findItem(R.id.menuDumpEditorWriteDump).setEnabled(
            !Common.useAsEditorOnly());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        int itemId = item.getItemId();
        if (itemId == R.id.menuDumpEditorSave) {
            saveDump();
            return true;
        } else if (itemId == R.id.menuDumpEditorAscii) {
            showAscii();
            return true;
        } else if (itemId == R.id.menuDumpEditorAccessConditions) {
            showAC();
            return true;
        } else if (itemId == R.id.menuDumpEditorValueBlocksAsInt) {
            decodeValueBlocks();
            return true;
        } else if (itemId == R.id.menuDumpEditorShare) {
            shareDump();
            return true;
        } else if (itemId == R.id.menuDumpEditorOpenValueBlockTool) {
            openValueBlockTool();
            return true;
        } else if (itemId == R.id.menuDumpEditorOpenAccessConditionTool) {
            openAccessConditionTool();
            return true;
        } else if (itemId == R.id.menuDumpEditorOpenBccTool) {
            openBccTool();
            return true;
        } else if (itemId == R.id.menuDumpEditorDecodeDateOfManuf) {
            decodeDateOfManuf();
            return true;
        } else if (itemId == R.id.menuDumpEditorWriteDump) {
            writeDump();
            return true;
        } else if (itemId == R.id.menuDumpEditorDiffDump) {
            diffDump();
            return true;
        } else if (itemId == R.id.menuDumpEditorSaveKeys) {
            saveKeys();
            return true;
        } else if (itemId == R.id.menuDumpEditorExportDump) {
            exportDump();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onUpdateColors(View view) {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }

        View focused = mLayout.getFocusedChild();
        int focusIndex = -1;
        if (focused != null) {
            focusIndex = mLayout.indexOfChild(focused);
        }
        initEditor(mLines);
        if (focusIndex != -1) {
            // Restore focused view.
            while (focusIndex >= 0
                && mLayout.getChildAt(focusIndex) == null) {
                focusIndex--;
            }
            if (focusIndex >= 0) {
                mLayout.getChildAt(focusIndex).requestFocus();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mDumpChanged) {
            new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_save_before_quitting_title)
                .setMessage(R.string.dialog_save_before_quitting)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.action_save,
                    (dialog, which) -> {
                        // Save.
                        mCloseAfterSuccessfulSave = true;
                        saveDump();
                    })
                .setNeutralButton(R.string.action_cancel,
                    (dialog, which) -> {
                        // Cancel. Do nothing.
                    })
                .setNegativeButton(R.string.action_dont_save,
                    (dialog, id) -> {
                        // Don't save.
                        finish();
                    }).show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveSuccessful() {
        if (mCloseAfterSuccessfulSave) {
            finish();
        }
        mDumpChanged = false;
    }

    @Override
    public void onSaveFailure() {
        mCloseAfterSuccessfulSave = false;
    }

    private void saveDump() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }

        if (mDumpName == null) {
            GregorianCalendar calendar = new GregorianCalendar();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                Locale.getDefault());
            fmt.setCalendar(calendar);
            String dateFormatted = fmt.format(calendar.getTime());
            mDumpName = "UID_" + mUID + "_" + dateFormatted + ".mct";
        }

        saveFile(mLines, mDumpName, true, R.string.dialog_save_dump_title,
            R.string.dialog_save_dump);
    }

    private void saveFile(final String[] data, final String fileName,
                          final boolean isDump, int titleId, int messageId) {
        String targetDir = (isDump) ? Common.DUMPS_DIR : Common.KEYS_DIR;
        final File path = Common.getFile(targetDir);
        final Context context = this;
        final IActivityThatReactsToSave activity = this;

        View dialogLayout = getLayoutInflater().inflate(
            R.layout.dialog_save_file,
            findViewById(android.R.id.content), false);
        TextView message = dialogLayout.findViewById(
            R.id.textViewDialogSaveFileMessage);
        final EditText input = dialogLayout.findViewById(
            R.id.editTextDialogSaveFileName);
        message.setText(messageId);
        input.setText(fileName);
        input.requestFocus();
        input.setSelection(0);

        new AlertDialog.Builder(this)
            .setTitle(titleId)
            .setIcon(android.R.drawable.ic_menu_save)
            .setView(dialogLayout)
            .setPositiveButton(R.string.action_save,
                (dialog, whichButton) -> {
                    if (input.getText() != null
                        && !input.getText().toString().equals("")
                        && !input.getText().toString().contains("/")) {
                        File file = new File(path.getPath(),
                            input.getText().toString());
                        Common.checkFileExistenceAndSave(file, data,
                            isDump, context, activity);
                        if (isDump) {
                            mDumpName = file.getName();
                        } else {
                            mKeysName = file.getName();
                        }
                    } else {
                        // Invalid file name.
                        Toast.makeText(context, R.string.info_invalid_file_name,
                            Toast.LENGTH_LONG).show();
                    }
                })
            .setNegativeButton(R.string.action_cancel,
                (dialog, whichButton) -> mCloseAfterSuccessfulSave = false)
            .show();
        onUpdateColors(null);
    }

    private int checkDumpAndUpdateLines() {
        ArrayList<String> checkedLines = new ArrayList<>();
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View child = mLayout.getChildAt(i);
            if (child instanceof AppCompatEditText) {
                String[] lines = ((EditText) child).getText().toString()
                    .split(System.getProperty("line.separator"));
                if (lines.length != 4 && lines.length != 16) {
                    // Not 4 or 16 lines.
                    return 1;
                }
                for (int j = 0; j < lines.length; j++) {
                    // Is hex or "-" == NO_KEY or NO_DATA.
                    if (!lines[j].matches("[0-9A-Fa-f-]+")) {
                        // Not pure hex.
                        return 2;
                    }
                    if (lines[j].length() != 32) {

                        return 3;
                    }
                    lines[j] = lines[j].toUpperCase(Locale.getDefault());
                    checkedLines.add(lines[j]);
                }
            } else if (child instanceof TextView) {
                TextView tv = (TextView) child;
                String tag = (String) tv.getTag();

                if (tag != null && tag.equals("real_header")) {
                    // Mark headers (sectors) with "+"
                    checkedLines.add("+Sector: "
                        + tv.getText().toString().split(": ")[1]);
                }
            }
        }
        // Update mLines.
        mLines = checkedLines.toArray(new String[0]);
        return 0;
    }


    @SuppressLint("SetTextI18n")
    private void initEditor(String[] lines) {
        int err = Common.isValidDump(lines, true);
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            Toast.makeText(this, R.string.info_editor_init_error, Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        boolean tmpDumpChanged = mDumpChanged;
        mLayout.removeAllViews();
        boolean isFirstBlock = false;
        AppCompatEditText et = null;
        ArrayList<SpannableString> blocks = new ArrayList<>(4);

        for (int i = 0; i < lines.length; i++) {
            if (lines[i].startsWith("+")) {
                // Line is a header (e.g., +Sector: n).
                isFirstBlock = lines[i].endsWith(" 0");
                String sectorNumber = lines[i].split(": ")[1];

                // Add sector header (TextView).
                TextView tv = new TextView(this);
                tv.setTextColor(Common.getThemeAccentColor(this));
                tv.setText(getString(R.string.text_sector) + ": " + sectorNumber);
                mLayout.addView(tv);

                if (i + 1 != lines.length && !lines[i + 1].startsWith("*")) {
                    // Add sector data (EditText).
                    et = new AppCompatEditText(this);
                    // Additional setup for the EditText...

                    // Get alphabetical readable form and log it
                    String readableData = convertHexToAscii(lines[i + 1]);
//                    Log.d("YourTag", "Logging Data: " + readableData);


                    et.setText(readableData);

                    mLayout.addView(et);
                    // Tag headers of real sectors (sectors containing data and not errors).
                    tv.setTag("real_header");
                }
            } else if (lines[i].startsWith("*")) {
                // Error Line: Line is a sector that could not be read.
                TextView tv = new TextView(this);
                tv.setTextColor(ContextCompat.getColor(this, R.color.red));
                tv.setText("   " + getString(R.string.text_no_key_io_error));
                tv.setTag("error");
                mLayout.addView(tv);
            } else {
                // Line is a block.
                if (i + 1 == lines.length || lines[i + 1].startsWith("+")) {
                    // Line is a sector trailer.
                    blocks.add(colorSectorTrailer(lines[i]));

                    // Add sector data to the EditText.
                    CharSequence text = "";
                    int j;
                    for (j = 0; j < blocks.size() - 1; j++) {
                        text = TextUtils.concat(text, blocks.get(j), "\n");
                    }
                    text = TextUtils.concat(text, blocks.get(j));
                    et.setText(text, BufferType.SPANNABLE);

                    blocks = new ArrayList<>(4);
                } else {
                    // Add data block.
                    blocks.add(colorDataBlock(lines[i], isFirstBlock));
                    isFirstBlock = false;
                }
            }
        }

        // Initialization of the editor is not a change.
        mDumpChanged = tmpDumpChanged;
    }

    private String convertHexToAscii(String hexData) {
        StringBuilder asciiText = new StringBuilder();

        for (int i = 0; i < hexData.length(); i += 2) {
            try {
                String hexPair = hexData.substring(i, i + 2);
                int decimalValue = Integer.parseInt(hexPair, 16);
                char asciiChar = (char) decimalValue;
                asciiText.append(asciiChar);
            } catch (NumberFormatException e) {
                // Handle non-hex characters, skip them or log as needed
                Log.e("YourTag", "Invalid hex character at index " + i);
            }
        }

        Log.e("DataofTAg", String.valueOf(asciiText));

        String asciiString = asciiText.toString();
        String[] parts = asciiString.split("/");
if (checkLogin.toString().matches("1")){
    if (parts.length >= 1) {
        String string1 = parts[0].trim();
        if (!string1.isEmpty() && isValidString(string1)) {
            cardData(string1);
            checkLogin="2";
            Log.d("YourTagstring1", "String1: " + string1);
        } else if (!string1.isEmpty() && isValidStringShort(string1)) {
            cardData(string1);
            Log.d("YourTagstring1", "String1: " + string1);
        } else {
            Log.e("YourTag", "Invalid format for String1: " + string1);
        }
    }
}
        String string2 = parts.length > 1 ? parts[1].trim() : "";  // 5199 (if exists)
        Log.d("YourTagstring2", "String2: " + string2);

        return asciiText.toString();
    }

    private boolean isValidString(String input) {
        // Define your pattern for validation (modify as needed)
        String pattern = "[A-Z]-\\d{4}[A-Z]";

        // Check if the input matches the pattern
        return input.matches(pattern);
    }

    private boolean isValidStringShort(String input) {
        // Define your pattern for validation (modify as needed)
        String pattern = "[A-Z]-\\d{4}";
        // Check if the input matches the pattern
        return input.matches(pattern);
    }


    public void cardData(String userValue) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                String userValue = params[0];
                ConnectionHelper connectionHelper = new ConnectionHelper(DumpEditor.this);
                Connection connect = connectionHelper.connectionclass();

                if (connect != null) {
                    try {
                        String query = "SELECT serialno, MAINID, name, memberID, type, status " +
                            "FROM TM_Memberinformation WHERE memberID = '" + userValue + "'";
                        Statement st = connect.createStatement();
                        ResultSet rs = st.executeQuery(query);

                        if (rs.next()) {
                            serialno = rs.getString("serialno");
                            MAINID = rs.getString("MAINID");
                            name = rs.getString("name");
                            memberID = rs.getString("memberID");
                            memberType = rs.getString("type");

                            // Ensure the status column exists
                            try {
                                status = rs.getString("status");
                            } catch (SQLException e) {
                                Log.e("ValueOFMenuModel", "Column 'status' does not exist: " + e.getMessage());
                                status = null;  // Handle missing status column
                            }

                            if (serialno != null && MAINID != null && name != null && memberID != null) {
                                return null;
                            }
                        }

                        // Data not found
                        tvInformation.setText("Card Not available in Database");
                        tvInformation.setVisibility(View.VISIBLE);
                    } catch (Exception e) {
                        // Handle the exception appropriately
                        Log.e("ValueOFMenuModel", "Exception while retrieving data: " + e.getMessage());
                        e.printStackTrace();
                    } finally {
                        try {
                            connect.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    // Connection failed
                    tvInformation.setText("Failed to connect to the database");
                    tvInformation.setVisibility(View.VISIBLE);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Update UI or perform any additional actions after the background task is complete
                if (serialno != null && MAINID != null && name != null && memberID != null) {
                    if (status != null && ("1".equals(status) || "2".equals(status))) {
                        llCardDeatils.setVisibility(View.VISIBLE);
                        llupper.setVisibility(View.VISIBLE);
                        tvCard.setVisibility(View.VISIBLE);
                        tvInformation.setVisibility(View.GONE);
                        tvName.setText(name);
                        tvId.setText(memberID);
                        tvCardHolderName.setText("Hi, " + name);
                        Log.e("ValueOFMenuModel", "Data found: " + serialno + " " + MAINID + " " +
                            name + " " + memberID + " " + memberType + " " + status);
                        cardAmountData(MAINID);
                    } else {
                        tvInformation.setText("You are not an active User");
                        tvInformation.setVisibility(View.VISIBLE);
                    }
                } else {
                    Log.e("ValueOFMenuModel", "Incomplete data for this row");
                }
            }
        }.execute(userValue);
    }

    public void cardAmountData(String uservalue) {

        ConnectionHelper connectionHelper = new ConnectionHelper(DumpEditor.this);
        Connection connect = connectionHelper.connectionclass();

        if (connect != null) {

            Log.e("gsvjgdv", "Connected to the database");

            try {

                String query = "SELECT couponno, amount, usercode FROM TM_IssueCoupon" +
                    " WHERE memberidno = '" + uservalue + "' AND Flag = 'Y'";

                Statement st = connect.createStatement();
                ResultSet rs = st.executeQuery(query);

                // Check if the result set is empty
                if (!rs.next()) {
                    // No data found
                    tvCardAmount.setText("Amount Not Available");
//                    tvInformation.setText("Card Amount  not exist in DataBase");
//                    tvInformation.setVisibility(View.VISIBLE);
//                    Toast.makeText(this, "Card not exist in CArd Amount DataBase", Toast.LENGTH_SHORT).show();
                } else {

                    do {
                         String amount = rs.getString("amount");
                         couponNo = rs.getString("couponno");
                         cashierCode = rs.getString("usercode");
//                        Flag = rs.getString("Flag");
                        if (couponNo != null && amount != null) {
                            Log.e("ValueOFAmount", "Data found: " + counponno + " " + amount+ " " + cashierCode);
                            tvCardAmount.setText("\u20B9 " + amount);
                            rlContinue.setVisibility(View.VISIBLE);
                            SharedPreferences sharedPreferences = getSharedPreferences("MySharedamount", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = sharedPreferences.edit();
                            myEdit.putString("amount", amount);
                            myEdit.apply();
                            myEdit.commit();
                        } else {
                            Log.e("ValueOFAmount", "Incomplete data for this row");
                        }
//                        if ("Y".equals(Flag)) {
//
//                        } else {
//                            tvCardAmount.setText("You are not a active User");
//
////                            Toast.makeText(getApplicationContext(), "Incorrect", Toast.LENGTH_SHORT).show();
//                        }


                    } while (rs.next());
                }

                connect.close();
            } catch (Exception e) {
                // Handle the exception appropriately
                Log.e("ValueOFAmount", "Exception while retrieving data: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            // Connection failed
            Log.e("gsvjgdv", "Failed to connect to the database");
        }
    }

    private void showAscii() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }
        // Get all data blocks (skip all Access Conditions).
        ArrayList<String> tmpDump = new ArrayList<>();
        for (int i = 0; i < mLines.length - 1; i++) {
            if (i + 1 != mLines.length
                && !mLines[i + 1].startsWith("+")) {
                tmpDump.add(mLines[i]);
            }
        }
        String[] dump = tmpDump.toArray(new String[0]);

        Intent intent = new Intent(this, HexToAscii.class);
        intent.putExtra(EXTRA_DUMP, dump);
        startActivity(intent);
    }

    private void showAC() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }
        // Get all Access Conditions (skip Data).
        ArrayList<String> tmpACs = new ArrayList<>();
        int lastSectorHeader = 0;
        for (int i = 0; i < mLines.length; i++) {
            if (mLines[i].startsWith("+")) {
                // Header.
                tmpACs.add(mLines[i]);
                lastSectorHeader = i;
            } else if (i + 1 == mLines.length
                || mLines[i + 1].startsWith("+")) {
                // Access Condition.
                if (i - lastSectorHeader > 4) {
                    // Access Conditions of a sector
                    // with more than 4 blocks --> Mark ACs with "*".
                    tmpACs.add("*" + mLines[i].substring(12, 20));
                } else {
                    tmpACs.add(mLines[i].substring(12, 20));
                }
            }
        }
        String[] ac = tmpACs.toArray(new String[0]);

        Intent intent = new Intent(this, AccessConditionDecoder.class);
        intent.putExtra(AccessConditionDecoder.EXTRA_AC, ac);
        startActivity(intent);
    }

    private void decodeValueBlocks() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }

        ArrayList<String> tmpVBs = new ArrayList<>();
        String header = "";
        int blockCounter = 0;
        for (String line : mLines) {
            if (line.startsWith("+")) {
                header = line;
                blockCounter = 0;
            } else {
                if (Common.isValueBlock(line)) {
                    // Header.
                    tmpVBs.add(header + ", Block: " + blockCounter);
                    // Value Block.
                    tmpVBs.add(line);
                }
                blockCounter++;
            }
        }

        if (tmpVBs.size() > 0) {
            String[] vb = tmpVBs.toArray(new String[0]);
            Intent intent = new Intent(this, ValueBlocksToInt.class);
            intent.putExtra(ValueBlocksToInt.EXTRA_VB, vb);
            startActivity(intent);
        } else {
            // No value blocks found.
            Toast.makeText(this, R.string.info_no_vb_in_dump,
                Toast.LENGTH_LONG).show();
        }
    }

    private void decodeDateOfManuf() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }
        if (mLines[0].equals("+Sector: 0") && !mLines[1].contains("-")) {
            int year;
            int week;
            SimpleDateFormat sdf = new SimpleDateFormat(
                "yy", Locale.getDefault());
            CharSequence styledText;
            try {
                year = Integer.parseInt(mLines[1].substring(30, 32));
                week = Integer.parseInt(mLines[1].substring(28, 30));
                int now = Integer.parseInt(sdf.format(new Date()));
                if (year >= 0 && year <= now && week >= 1 && week <= 53) {
                    // Calculate the date of manufacture.
                    Calendar calendar = Calendar.getInstance();
                    calendar.clear();
                    calendar.set(Calendar.WEEK_OF_YEAR, week);
                    // year + 2000: Yep, hardcoded. Hopefully MFC is dead
                    // around year 3000. :)
                    calendar.set(Calendar.YEAR, year + 2000);
                    sdf.applyPattern("dd.MM.yyyy");
                    String startDate = sdf.format(calendar.getTime());
                    calendar.add(Calendar.DATE, 6);
                    String endDate = sdf.format(calendar.getTime());

                    styledText = HtmlCompat.fromHtml(
                        getString(R.string.dialog_date_of_manuf, startDate, endDate),
                        HtmlCompat.FROM_HTML_MODE_LEGACY);
                } else {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException ex) {
                // Error. Tag has wrong data set as date of manufacture.
                styledText = getText(R.string.dialog_date_of_manuf_error);
            }
            // Show dialog.
            new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_date_of_manuf_title)
                .setMessage(styledText)
                .setIcon(android.R.drawable.ic_dialog_info)
                .setPositiveButton(R.string.action_ok,
                    (dialog, which) -> {
                        // Do nothing.
                    }).show();
        } else {
            // Error. There is no block 0.
            Toast.makeText(this, R.string.info_block0_missing,
                Toast.LENGTH_LONG).show();
        }
    }

    private void openValueBlockTool() {
        Intent intent = new Intent(this, ValueBlockTool.class);
        startActivity(intent);
    }

    private void openAccessConditionTool() {
        Intent intent = new Intent(this, AccessConditionTool.class);
        startActivity(intent);
    }

    private void openBccTool() {
        Intent intent = new Intent(this, BccTool.class);
        startActivity(intent);
    }

    private void writeDump() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }
        Intent intent = new Intent(this, WriteTag.class);
        intent.putExtra(WriteTag.EXTRA_DUMP, mLines);
        startActivity(intent);
    }

    private void diffDump() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }
        Intent intent = new Intent(this, DiffTool.class);
        intent.putExtra(DiffTool.EXTRA_DUMP, mLines);
        startActivity(intent);
    }

    private void shareDump() {
        File file = saveDumpToTemp();
        if (file == null || !file.exists() && file.isDirectory()) {
            return;
        }
        // Share file.
        Common.shareTextFile(this, file);
    }

    private void exportDump() {
        File file = saveDumpToTemp();
        if (file == null || !file.exists() && file.isDirectory()) {
            return;
        }
        Intent intent = new Intent(this, ImportExportTool.class);
        intent.putExtra(ImportExportTool.EXTRA_FILE_PATH, file.getAbsolutePath());
        intent.putExtra(ImportExportTool.EXTRA_IS_DUMP_FILE, true);
        startActivity(intent);
    }

    private File saveDumpToTemp() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return null;
        }

        String fileName;
        if (mDumpName == null) {
            // The dump has no name. Use date and time as name.
            GregorianCalendar calendar = new GregorianCalendar();
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss",
                Locale.getDefault());
            fmt.setCalendar(calendar);
            fileName = fmt.format(calendar.getTime());
        } else {
            fileName = mDumpName;
        }
        // Save file to tmp directory.
        File file = Common.getFile(Common.TMP_DIR + "/" + fileName);
        if (!Common.saveFile(file, mLines, false)) {
            Toast.makeText(this, R.string.info_save_error,
                Toast.LENGTH_LONG).show();
            return null;
        }
        return file;
    }

    private void saveKeys() {
        int err = checkDumpAndUpdateLines();
        if (err != 0) {
            Common.isValidDumpErrorToast(err, this);
            return;
        }

        HashSet<String> tmpKeys = new HashSet<>();
        for (int i = 0; i < mLines.length; i++) {
            if (i + 1 == mLines.length || mLines[i + 1].startsWith("+")) {
                // Sector trailer.
                String keyA = mLines[i].substring(0, 12).toUpperCase();
                String keyB = mLines[i].substring(20).toUpperCase();
                if (!keyA.equals(MCReader.NO_KEY)) {
                    tmpKeys.add(keyA);
                }
                if (!keyB.equals(MCReader.NO_KEY)) {
                    tmpKeys.add(keyB);
                }
            }
        }
        String[] keys = tmpKeys.toArray(new String[0]);

        if (mKeysName == null) {
            if (mDumpName == null) {
                mKeysName = "UID_" + mUID;
            } else {
                mKeysName = mDumpName;
            }
        }
        mKeysName += ".keys";

        saveFile(keys, mKeysName, false, R.string.dialog_save_keys_title,
            R.string.dialog_save_keys);
    }

    private SpannableString colorDataBlock(String data, boolean hasUID) {
        SpannableString ret;
        if (hasUID) {

            ret = new SpannableString(TextUtils.concat(
                Common.colorString(data,
                    ContextCompat.getColor(this, R.color.purple))));
        } else {
            if (Common.isValueBlock(data)) {
                // Value block.
                ret = Common.colorString(data,
                    ContextCompat.getColor(this, R.color.yellow));
            } else {
                // Just data.
                ret = new SpannableString(data);
            }
        }
        return ret;
    }


    private SpannableString colorSectorTrailer(String data) {
        // Get sector trailer colors.
        int colorKeyA = ContextCompat.getColor(this, R.color.light_green);
        int colorKeyB = ContextCompat.getColor(this, R.color.dark_green);
        int colorAC = ContextCompat.getColor(this, R.color.orange);
        try {
            SpannableString keyA = Common.colorString(
                data.substring(0, 12), colorKeyA);
            SpannableString keyB = Common.colorString(
                data.substring(20), colorKeyB);
            SpannableString ac = Common.colorString(
                data.substring(12, 18), colorAC);
            return new SpannableString(
                TextUtils.concat(keyA, ac,
                    data.substring(18, 20), keyB));
        } catch (IndexOutOfBoundsException e) {
            Log.d(LOG_TAG, "Error while coloring " +
                "sector trailer");
        }
        return new SpannableString(data);
    }
}
