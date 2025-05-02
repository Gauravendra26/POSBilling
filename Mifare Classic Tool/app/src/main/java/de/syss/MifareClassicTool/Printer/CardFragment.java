package de.syss.MifareClassicTool.Printer;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import android.view.View;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;

import com.zcs.sdk.DriverManager;
import com.zcs.sdk.SdkData;
import com.zcs.sdk.SdkResult;
import com.zcs.sdk.card.CardInfoEntity;
import com.zcs.sdk.card.CardReaderManager;
import com.zcs.sdk.card.CardReaderTypeEnum;
import com.zcs.sdk.card.CardSlotNoEnum;
import com.zcs.sdk.card.ICCard;
import com.zcs.sdk.card.MagCard;
import com.zcs.sdk.card.RfCard;
import com.zcs.sdk.listener.OnSearchCardListener;
import com.zcs.sdk.util.StringUtils;

import de.syss.MifareClassicTool.R;


public class CardFragment extends PreferenceFragment {

    private static final String TAG = "CardFragment";

    private static final String KEY_IC_CARD = "IC_card_key";
    private static final String KEY_PSAM_CARD = "psam_card_key";
    private static final String KEY_MAGNETIC_CARD = "magnetic_card_key";
    private static final String KEY_CONTACTLESS_CARD = "contactless_card_key";

    private static final int READ_TIMEOUT = 60 * 1000;

    private ProgressDialog mProgressDialog;

    private com.zcs.sdk.DriverManager mDriverManager;
    private CardReaderManager mCardReadManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_card);
        mDriverManager = DriverManager.getInstance();
        mCardReadManager = mDriverManager.getCardReadManager();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        findPreference(KEY_IC_CARD).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                searchICCard();
                return true;
            }
        });

        findPreference(KEY_PSAM_CARD).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                searchPSAM1();
                return true;
            }
        });

        findPreference(KEY_MAGNETIC_CARD).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                searchMagnetCard();
                return true;
            }
        });

        findPreference(KEY_CONTACTLESS_CARD).setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                searchRfCard();
                return true;
            }
        });
    }

    private void searchICCard() {
        showSearchCardDialog(R.string.waiting, R.string.msg_ic_card);
        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(CardReaderTypeEnum.IC_CARD, READ_TIMEOUT, mICCardSearchCardListener);
    }

    private OnSearchCardListener mICCardSearchCardListener = new OnSearchCardListener() {
        @Override
        public void onCardInfo(CardInfoEntity cardInfoEntity) {
            mProgressDialog.dismiss();
            readICCard();
        }

        @Override
        public void onError(int i) {
            mProgressDialog.dismiss();
            showReadICCardErrorDialog(i);
        }

        @Override
        public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {

        }
    };

    public static final byte[] APDU_SEND_IC = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x31, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0X00};
    private void readICCard() {
        ICCard icCard = mCardReadManager.getICCard();
        int result = icCard.icCardReset(CardSlotNoEnum.SDK_ICC_USERCARD);
        if (result == SdkResult.SDK_OK) {
            int[] recvLen = new int[1];
            byte[] recvData = new byte[300];
            result = icCard.icExchangeAPDU(CardSlotNoEnum.SDK_ICC_USERCARD, APDU_SEND_IC, recvData, recvLen);
            if (result == SdkResult.SDK_OK) {
                final String apduRecv = StringUtils.convertBytesToHex(recvData).substring(0, recvLen[0] * 2);
                CardFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.show(getActivity(), "Read IC card result", apduRecv);
                    }
                });
            } else {
                showReadICCardErrorDialog(result);
            }
        } else {
            showReadICCardErrorDialog(result);
        }
        icCard.icCardPowerDown(CardSlotNoEnum.SDK_ICC_USERCARD);
    }

    private void showReadICCardErrorDialog(final int errorCode) {
        CardFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.show(getActivity(), "Read IC card failed", "Error code = " + errorCode);
            }
        });
    }

    private void searchPSAM1() {
        showSearchCardDialog(R.string.waiting, R.string.msg_psam_card);
        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(CardReaderTypeEnum.PSIM1, READ_TIMEOUT, mPSAM1SearchCardListener);
    }

    private OnSearchCardListener mPSAM1SearchCardListener = new OnSearchCardListener() {
        @Override
        public void onCardInfo(CardInfoEntity cardInfoEntity) {
            mProgressDialog.dismiss();
            readPSAM1();
        }

        @Override
        public void onError(int i) {
            mProgressDialog.dismiss();
            showReadPSAM1ErrorDialog(i);
        }

        @Override
        public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {

        }
    };

    public static final byte[] APDU_SEND_RANDOM = {0x00, (byte) 0x84, 0x00, 0x00, 0x08};
    private void readPSAM1() {
        ICCard icCard = mCardReadManager.getICCard();
        int result = icCard.icCardReset(CardSlotNoEnum.SDK_ICC_SAM1);
        if (result == SdkResult.SDK_OK) {
            int[] recvLen = new int[1];
            byte[] recvData = new byte[300];
            result = icCard.icExchangeAPDU(CardSlotNoEnum.SDK_ICC_SAM1, APDU_SEND_RANDOM, recvData, recvLen);
            if (result == SdkResult.SDK_OK) {
                final String apduRecv = StringUtils.convertBytesToHex(recvData).substring(0, recvLen[0] * 2);
                CardFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.show(getActivity(), "Read PSAM1 result", apduRecv);
                    }
                });
            } else {
                showReadPSAM1ErrorDialog(result);
            }
        } else {
            showReadPSAM1ErrorDialog(result);
        }
        icCard.icCardPowerDown(CardSlotNoEnum.SDK_ICC_SAM1);
    }

    private void showReadPSAM1ErrorDialog(final int errorCode) {
        CardFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.show(getActivity(), "Read PSAM1 failed", "Error code = " + errorCode);
            }
        });
    }

    private void searchMagnetCard() {
        showSearchCardDialog(R.string.waiting, R.string.msg_magnet_card);
        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(CardReaderTypeEnum.MAG_CARD, READ_TIMEOUT, mMagnetCardSearchCardListener);
    }

    private OnSearchCardListener mMagnetCardSearchCardListener = new OnSearchCardListener() {
        @Override
        public void onCardInfo(CardInfoEntity cardInfoEntity) {
            mProgressDialog.dismiss();
            readMagnetCard();
        }

        @Override
        public void onError(int i) {
            mProgressDialog.dismiss();
            showReadMagnetCardErrorDialog(i);
        }

        @Override
        public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {

        }
    };
    private void readMagnetCard() {
        MagCard magCard = mCardReadManager.getMAGCard();
        final CardInfoEntity cardInfoEntity = magCard.getMagReadData();
        if (cardInfoEntity.getResultcode() == SdkResult.SDK_OK) {
            CardFragment.this.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    DialogUtils.show(getActivity(), "Read magnetic card result", cardInfoToString(cardInfoEntity));
                }
            });
        } else {
            showReadMagnetCardErrorDialog(cardInfoEntity.getResultcode());
        }
        magCard.magCardClose();
    }

    private void showReadMagnetCardErrorDialog(final int errorCode) {
        CardFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.show(getActivity(), "Read magnetic card failed", "Error code = " + errorCode);
            }
        });
    }

    private void searchRfCard() {
        showSearchCardDialog(R.string.waiting, R.string.msg_contactless_card);
        mCardReadManager.cancelSearchCard();
        mCardReadManager.searchCard(CardReaderTypeEnum.RF_CARD, READ_TIMEOUT, mRfCardSearchCardListener);
    }

    private OnSearchCardListener mRfCardSearchCardListener = new OnSearchCardListener() {
        @Override
        public void onCardInfo(CardInfoEntity cardInfoEntity) {
            mProgressDialog.dismiss();
            byte rfCardType = cardInfoEntity.getRfCardType();
            readRfCard(rfCardType);
        }

        @Override
        public void onError(int i) {
            mProgressDialog.dismiss();
            showReadRfCardErrorDialog(i);
        }

        @Override
        public void onNoCard(CardReaderTypeEnum cardReaderTypeEnum, boolean b) {

        }
    };

    public static final byte[] APDU_SEND_RF = {0x00, (byte) 0xA4, 0x04, 0x00, 0x0E, 0x32, 0x50, 0x41, 0x59, 0x2E, 0x53, 0x59, 0x53, 0x2E, 0x44, 0x44, 0x46, 0x30, 0x31, 0x00};
    public static final byte[] APDU_SEND_FELICA = {0x10, 0x06, 0x01, 0x2E, 0x45, 0x76, (byte) 0xBA, (byte) 0xC5, 0x45, 0x2B, 0x01, 0x09, 0x00, 0x01, (byte) 0x80, 0x00};
    private void readRfCard(final byte rfCardType) {
        RfCard rfCard = mCardReadManager.getRFCard();
        int result = rfCard.rfReset();
        if(result == SdkResult.SDK_OK) {
            byte[] apduSend;
            if (rfCardType == SdkData.RF_TYPE_FELICA) { // felica card
                apduSend = APDU_SEND_FELICA;
            } else {
                apduSend = APDU_SEND_RF;
            }
            int[] recvLen = new int[1];
            byte[] recvData = new byte[300];
            result = rfCard.rfExchangeAPDU(apduSend, recvData, recvLen);
            if(result == SdkResult.SDK_OK) {
                final String apduRecv = StringUtils.convertBytesToHex(recvData).substring(0, recvLen[0] * 2);
                CardFragment.this.getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        DialogUtils.show(getActivity(), "Read contactless card result",
                            "Card type: " + rfCardTypeToString(rfCardType) + "\n" +
                                "Result: " + apduRecv);
                    }
                });
            } else {
                showReadRfCardErrorDialog(result);
            }
        } else {
            showReadRfCardErrorDialog(result);
        }
        rfCard.rfCardPowerDown();
    }

    private void showReadRfCardErrorDialog(final int errorCode) {
        CardFragment.this.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DialogUtils.show(getActivity(), "Read contactless card failed", "Error code = " + errorCode);
            }
        });
    }

    private void showSearchCardDialog(@StringRes int title, @StringRes int msg) {
        mProgressDialog = (ProgressDialog) DialogUtils.showProgress(getActivity(), getString(title), getString(msg), new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mCardReadManager.cancelSearchCard();
            }
        });
    }

    private static String cardInfoToString(CardInfoEntity cardInfoEntity) {
        if (cardInfoEntity == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append("Resultcode:\t" + cardInfoEntity.getResultcode() + "\n")
            .append(cardInfoEntity.getCardExistslot() == null ? "" : "Card type:\t" + cardInfoEntity.getCardExistslot().name() + "\n")
            .append(cardInfoEntity.getCardNo() == null ? "" : "Card no:\t" + cardInfoEntity.getCardNo() + "\n")
            .append(cardInfoEntity.getRfCardType() == 0 ? "" : "Rf card type:\t" + cardInfoEntity.getRfCardType() + "\n")
            .append(cardInfoEntity.getRFuid() == null ? "" : "RFUid:\t" + new String(cardInfoEntity.getRFuid()) + "\n")
            .append(cardInfoEntity.getAtr() == null ? "" : "Atr:\t" + cardInfoEntity.getAtr() + "\n")
            .append(cardInfoEntity.getTk1() == null ? "" : "Track1:\t" + cardInfoEntity.getTk1() + "\n")
            .append(cardInfoEntity.getTk2() == null ? "" : "Track2:\t" + cardInfoEntity.getTk2() + "\n")
            .append(cardInfoEntity.getTk3() == null ? "" : "Track3:\t" + cardInfoEntity.getTk3() + "\n")
            .append(cardInfoEntity.getExpiredDate() == null ? "" : "expiredDate:\t" + cardInfoEntity.getExpiredDate() + "\n")
            .append(cardInfoEntity.getServiceCode() == null ? "" : "serviceCode:\t" + cardInfoEntity.getServiceCode());
        return sb.toString();
    }

    private String rfCardTypeToString(byte rfCardType) {
        String type = "";
        switch (rfCardType) {
            case SdkData.RF_TYPE_A:
                type = "RF_TYPE_A";
                break;
            case SdkData.RF_TYPE_B:
                type = "RF_TYPE_B";
                break;
            case SdkData.RF_TYPE_MEMORY_A:
                type = "RF_TYPE_MEMORY_A";
                break;
            case SdkData.RF_TYPE_FELICA:
                type = "RF_TYPE_FELICA";
                break;
            case SdkData.RF_TYPE_MEMORY_B:
                type = "RF_TYPE_MEMORY_B";
                break;
        }
        return type;
    }
}
