package com.woleapp.netpos.qrgenerator.viewmodels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.auth0.android.jwt.JWT
import com.google.gson.Gson
import com.pixplicity.easyprefs.library.Prefs
import com.woleapp.netpos.qrgenerator.db.AppDatabase
import com.woleapp.netpos.qrgenerator.model.*
import com.woleapp.netpos.qrgenerator.network.QRRepository
import com.woleapp.netpos.qrgenerator.utils.Event
import com.woleapp.netpos.qrgenerator.utils.PREF_USER
import com.woleapp.netpos.qrgenerator.utils.Resource
import com.woleapp.netpos.qrgenerator.utils.Singletons
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import retrofit2.HttpException
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@HiltViewModel
class QRViewModel @Inject constructor(
    private val qrRepository: QRRepository,
) : ViewModel() {
    private val disposable = CompositeDisposable()

    private var _loginResponse: MutableLiveData<Resource<LoginResponse>> =
        MutableLiveData()
    val loginResponse: LiveData<Resource<LoginResponse>> get() = _loginResponse

    private var _registerResponse: MutableLiveData<Resource<GeneralResponse>> =
        MutableLiveData()
    val registerResponse: LiveData<Resource<GeneralResponse>> get() = _registerResponse

    private var _generateQrResponse: MutableLiveData<Resource<GenerateQRResponse>> =
        MutableLiveData()
    val generateQrResponse: LiveData<Resource<GenerateQRResponse>> get() = _generateQrResponse

    private var _cardSchemeResponse: MutableLiveData<Resource<CardSchemeResponse>> =
        MutableLiveData()
    val cardSchemeResponse: LiveData<Resource<CardSchemeResponse>> get() = _cardSchemeResponse

    private var _issuingBankResponse: MutableLiveData<Resource<BankCardResponse>> =
        MutableLiveData()
    val issuingBankResponse: LiveData<Resource<BankCardResponse>> get() = _issuingBankResponse

    private var _transactionResponse: MutableLiveData<Resource<TransactionResponse>> =
        MutableLiveData()
    val transactionResponse: LiveData<Resource<TransactionResponse>> get() = _transactionResponse

    private var _merchantResponse: MutableLiveData<Resource<MerchantResponse>> =
        MutableLiveData()
    val merchantResponse: LiveData<Resource<MerchantResponse>> get() = _merchantResponse

    private var _allMerchantResponse: MutableLiveData<Resource<AllMerchantResponse>> =
        MutableLiveData()
    val allMerchantResponse: LiveData<Resource<AllMerchantResponse>> get() = _allMerchantResponse

    private val _generateQrMessage = MutableLiveData<Event<String>>()
    val generateQrMessage: LiveData<Event<String>>
        get() = _generateQrMessage

    private val _registerMessage = MutableLiveData<Event<String>>()
    val registerMessage: LiveData<Event<String>>
        get() = _registerMessage

    private val _loginMessage = MutableLiveData<Event<String>>()
    val loginMessage: LiveData<Event<String>>
        get() = _loginMessage

    private val _transactionMessage = MutableLiveData<Event<String>>()
    val transactionMessage: LiveData<Event<String>>
        get() = _transactionMessage

    private val _merchantMessage = MutableLiveData<Event<String>>()
    val merchantMessage: LiveData<Event<String>>
        get() = _merchantMessage

    val cardSchemes = arrayListOf<Row>()

    val issuingBank = arrayListOf<RowX>()

    private lateinit var allUsers: LiveData<List<GenerateQRResponse>>

    //     lateinit var transactionLIst : List<Transaction>
    private lateinit var _merchantLIst: List<Merchant>
    val merchantLIst: List<Merchant>
        get() = _merchantLIst
    private lateinit var _allMerchantLIst: List<Merchant>
    val allMerchantLIst: List<Merchant>
        get() = _allMerchantLIst

    private lateinit var formattedDate: String


    fun login(loginRequest: LoginRequest) {
        _loginResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.login(loginRequest)
                .flatMap {
                    Timber.e(it.toString())
                    if (!it.success) {
                        throw Exception("Login Failed, Check Credentials")
                    }
                    val userToken = it.token
                    val userTokenDecoded = JWT(userToken)
                    val user = User().apply {
                        this.fullname =
                            if (userTokenDecoded.claims.containsKey("fullname")) userTokenDecoded.getClaim(
                                "fullname"
                            ).asString().toString() else " "
                        this.mobile_phone =
                            if (userTokenDecoded.claims.containsKey("mobile_phone")) userTokenDecoded.getClaim(
                                "mobile_phone"
                            ).asString().toString() else " "
                        this.email =
                            if (userTokenDecoded.claims.containsKey("email")) userTokenDecoded.getClaim(
                                "email"
                            ).asString().toString() else " "
                        this.id =
                            if (userTokenDecoded.claims.containsKey("id")) userTokenDecoded.getClaim(
                                "id"
                            ).asInt()!! else 1
                    }
                    // Log.d("FULLNAME","${user}")
                    Single.just(user)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        Prefs.putString(PREF_USER, Gson().toJson(it))
                        _loginResponse.postValue(Resource.success(it) as Resource<LoginResponse>)
                        _loginMessage.value = Event("User logged in successfully")
                        //  Log.d("EEEMAIL","${it.email}")
                    }
                    error?.let {
                        _loginResponse.postValue(Resource.error(null))
                        // Timber.d("ERROR==${it.localizedMessage}")
                        (it as? HttpException).let { httpException ->
                            val errorMessage = httpException?.response()?.errorBody()?.string()
                                ?: "{\"message\":\"Unexpected error\"}"
                            _loginMessage.value = Event(
                                try {
                                    Gson().fromJson(errorMessage, ErrorModel::class.java).message
                                        ?: "Error"
                                } catch (e: Exception) {
                                    "Error"
                                }
                            )
                            //Timber.e("SHOWME--->$errorMessage")
                        }
                    }
                }
        )
    }

    fun register(registerRequest: RegisterRequest) {
        _registerResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.register(registerRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        _registerResponse.postValue(Resource.success(it))
                        _registerMessage.value = Event("Registration Successful")
                    }
                    error?.let {
                        _registerResponse.postValue(Resource.error(null))
                        // Timber.d("ERROR==${it.localizedMessage}")
                        (it as? HttpException).let { httpException ->
                            val errorMessage = httpException?.response()?.errorBody()?.string()
                                ?: "{\"message\":\"Unexpected error\"}"
                            _registerMessage.value = Event(
                                try {
                                    Gson().fromJson(errorMessage, ErrorModel::class.java).message
                                        ?: "Error"
                                } catch (e: Exception) {
                                    "Error"
                                }
                            )
                            //Timber.e("SHOWME--->$errorMessage")
                        }
                    }
                }
        )
    }

    fun generateQR(qrModelRequest: QrModelRequest, context: Context) {
        _generateQrResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.generateQR(qrModelRequest)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        //    Prefs.putString(PREF_QR, Gson().toJson(it))
                        _generateQrResponse.postValue(Resource.success(it))
                        _generateQrMessage.value = Event("Success")
                        val c = Calendar.getInstance().time
                        val formatter =
                            SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
                        formattedDate = formatter.format(c)
                        val userId = Singletons().getCurrentlyLoggedInUser()?.id.toString()
                        val generalResponse = GenerateQRResponse(
                            qr_code_id = it.qr_code_id,
                            data = it.data,
                            success = true,
                            card_scheme = it.card_scheme,
                            issuing_bank = it.issuing_bank,
                            date = formattedDate
                        )
                        val dataQREntity = DomainQREntity(it.qr_code_id!!, userId, generalResponse)
                        AppDatabase.getDatabaseInstance(context).qrDao()
                            .insertQrCode(dataQREntity)
                    }
                    error?.let {
                        _generateQrResponse.postValue(Resource.error(null))
                        // Timber.d("ERROR==${it.localizedMessage}")
                        (it as? HttpException).let { httpException ->
                            val errorMessage = httpException?.response()?.errorBody()?.string()
                                ?: "{\"message\":\"Unexpected error\"}"
                            _generateQrMessage.value = Event(
                                try {
                                    Gson().fromJson(errorMessage, ErrorModel::class.java).message
                                        ?: "Error"
                                } catch (e: Exception) {
                                    "Error"
                                }
                            )
                            //Timber.e("SHOWME--->$errorMessage")
                        }
                    }
                }
        )
    }

    fun getCardSchemes() {
        _cardSchemeResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.getCardSchemes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        _cardSchemeResponse.postValue(Resource.success(it))
                        //  _message.value = Event("Success")
                        for (i in 0 until it.rows.size) {
                            it.rows[i].let { it1 -> cardSchemes.add(it1) }

                        }
                    }
                    error?.let {
                        _cardSchemeResponse.postValue(Resource.error(null))
                        // Timber.d("ERROR==${it.localizedMessage}")
//                        (it as? HttpException).let { httpException ->
//                            val errorMessage = httpException?.response()?.errorBody()?.string()
//                                ?: "{\"message\":\"Unexpected error\"}"
//                            _message.value = Event(
//                                try {
//                                    gson.fromJson(errorMessage, ErrorModel::class.java).message
//                                } catch (e: Exception) {
//                                    "Error"
//                                }
//                            )
//                            //Timber.e("SHOWME--->$errorMessage")
//                        }
                    }
                }
        )
    }

    fun getCardBanks() {
        _issuingBankResponse.postValue(Resource.loading(null))
        //saveAccountNumber(accountNumber)
        disposable.add(
            qrRepository.getCardBanks()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        _issuingBankResponse.postValue(Resource.success(it))
                        //     _message.value = Event("Success")
                        for (i in 0 until it.rows.size) {
                            it.rows[i].let { it1 -> issuingBank.add(it1) }
                        }
                    }
                    error?.let {
                        _issuingBankResponse.postValue(Resource.error(null))
                        // Timber.d("ERROR==${it.localizedMessage}")
//                        (it as? HttpException).let { httpException ->
//                            val errorMessage = httpException?.response()?.errorBody()?.string()
//                                ?: "{\"message\":\"Unexpected error\"}"
//                            _message.value = Event(
//                                try {
//                                    gson.fromJson(errorMessage, ErrorModel::class.java).message
//                                } catch (e: Exception) {
//                                    "Error"
//                                }
//                            )
//                            //Timber.e("SHOWME--->$errorMessage")
//                        }
                    }
                }
        )
    }

    fun getEachTransaction(qrCodeID: String) {
        _transactionResponse.postValue(Resource.loading(null))
        disposable.add(
            qrRepository.getAllTransaction(qrCodeID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { data, error ->
                    data?.let {
                        _transactionResponse.postValue(Resource.success(it))
//                         transactionLIst = arrayListOf()
//                        transactionLIst = it.data.rows
                        //     _message.value = Event("Success")
                    }
                    error?.let {
                        _transactionResponse.postValue(Resource.error(null))
                        // Timber.d("ERROR==${it.localizedMessage}")
                        (it as? HttpException).let { httpException ->
                            val errorMessage = httpException?.response()?.errorBody()?.string()
                                ?: "{\"message\":\"Unexpected error\"}"
                            _transactionMessage.value = Event(
                                try {
                                    Gson().fromJson(errorMessage, ErrorModel::class.java).message
                                } catch (e: Exception) {
                                    "Error"
                                }
                            )
                        }
                    }
                }
        )
    }


//    fun getMerchant(searchQuery:String) {
//        _merchantResponse.postValue(Resource.loading(null))
//        disposable.add(
//            qrRepository.getMerchant(searchQuery)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { data, error ->
//                    data?.let {
//                        _merchantResponse.postValue(Resource.success(it))
//                        _merchantLIst = arrayListOf()
//                        _merchantLIst = it.data.rows
//                   //     _message.value = Event("Success")
//                    }
//                    error?.let {
//                        _merchantResponse.postValue(Resource.error(null))
//                       // Timber.d("ERROR==${it.localizedMessage}")
//                        (it as? HttpException).let { httpException ->
//                            val errorMessage = httpException?.response()?.errorBody()?.string()
//                                ?: "{\"message\":\"Unexpected error\"}"
//                            _merchantMessage.value = Event(
//                                try {
//                                    Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                                } catch (e: Exception) {
//                                    "Error"
//                                }
//                            )
//                        }
//                    }
//                }
//        )
//    }

//    fun getAllMerchant() {
//        _allMerchantResponse.postValue(Resource.loading(null))
//        disposable.add(
//            qrRepository.getAllMerchant()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { data, error ->
//                    data?.let {
//                        _allMerchantResponse.postValue(Resource.success(it))
//                        _allMerchantLIst = arrayListOf()
//                        _allMerchantLIst = it.data
//                        //     _message.value = Event("Success")
//                    }
//                    error?.let {
//                        _allMerchantResponse.postValue(Resource.error(null))
//                        // Timber.d("ERROR==${it.localizedMessage}")
//                        (it as? HttpException).let { httpException ->
//                            val errorMessage = httpException?.response()?.errorBody()?.string()
//                                ?: "{\"message\":\"Unexpected error\"}"
//                            _merchantMessage.value = Event(
//                                try {
//                                    Gson().fromJson(errorMessage, ErrorModel::class.java).message
//                                } catch (e: Exception) {
//                                    "Error"
//                                }
//                            )
//                        }
//                    }
//                }
//        )
//    }


    fun clear() {
        _generateQrResponse.postValue(null)
    }

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }
}