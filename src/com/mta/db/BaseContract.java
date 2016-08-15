package com.mta.db;

import android.provider.BaseColumns;

public final class BaseContract {
	// To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public BaseContract() {}

    /* Inner class that defines the table contents */
    public static abstract class Model implements BaseColumns {
    }
}
