package josiak.android.example.cryptocurrency.charts.data

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey

/**
 * Created by Kuba on 2018-06-11.
 */
@Entity(tableName = "favsCrypto",
        foreignKeys = [(ForeignKey(
        entity = Crypto::class,
        parentColumns = ["id"],
        childColumns = ["id"]))])
data class CryptoFavs(
        @PrimaryKey val id: Long,
        val favourite: Int
)