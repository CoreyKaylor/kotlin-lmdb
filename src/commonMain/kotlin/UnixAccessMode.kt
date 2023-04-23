/**
 * Unix file access privileges
 */
enum class UnixAccessMode(val option: UShort) {
    /**
     * S_IRUSR
     */
    OwnerRead(0x0100u),

    /**
     * S_IWUSR
     */
    OwnerWrite(0x0080u),

    /**
     * S_IXUSR
     */
    OwnerExec(0x0040u),

    /**
     * S_IRGRP
     */
    GroupRead(0x0020u),

    /// <summary>
    /// S_IWGRP
    /// </summary>
    /**
     * S_IWGRP
     */
    GroupWrite(0x0010u),

    /**
     * S_IXGRP
     */
    GroupExec(0x0008u),

    /**
     * S_IROTH
     */
    OtherRead(0x0004u),

    /**
     * S_IWOTH
     */
    OtherWrite(0x0002u),

    /**
     * S_IXOTH
     */
    OtherExec(0x0001u)
}

val defaultUnixAccessMode = UnixAccessMode.OwnerRead or UnixAccessMode.OwnerWrite or UnixAccessMode.GroupRead or UnixAccessMode.GroupWrite or UnixAccessMode.OtherRead or UnixAccessMode.OtherWrite
infix fun UnixAccessMode.or(other: UnixAccessMode) = this.option or other.option
infix fun UnixAccessMode.and(other: UnixAccessMode) = this.option and other.option
infix fun UShort.or(other: UnixAccessMode) = this or other.option
infix fun UShort.and(other: UnixAccessMode) = this and other.option
