import com.google.gson.annotations.SerializedName

data class ThanaResponse(
    @SerializedName("message") val message: String,
    @SerializedName("thanas") val thanas: List<Thana>
)

data class Thana(
    @SerializedName("id") val id: Int,
    @SerializedName("name") val name: String
)