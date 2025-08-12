package kr.co.hyunwook.pet_grow_daily.analytics

object EventConstants {

    //사진 등록
    const val UPLOAD_IMAGE_EVENT = "upload_image"
    const val IS_PUBLIC_PROPERTY = "is_public"

    //앨범 탭 진입
    const val ENTER_ALBUM_TAB_EVENT = "enter_album_tab"
    const val ALBUM_COUNT_PROPERTY = "album_count"

    //주문 플로우
    const val CLICK_ORDER_PRODUCT_TYPE_EVENT = "click_order_product_type"
    const val PRODUCT_TYPE_PROPERTY = "product_type"

    const val CLICK_ORDER_START_EVENT = "click_order_start"

    const val CLICK_ALBUM_SELECT_DONE_EVENT = "click_album_select_done"

    const val CLICK_ALBUM_LAYOUT_DONE_EVENT = "click_album_layout_done"
    const val LAYOUT_TYPE_PROPERTY = "layout_Type"

    const val CLICK_DELIVERY_DONE_EVENT = "click_delivery_done"

    const val START_ORDER_PG_EVENT = "start_order_pg"

    const val DONE_ORDER_PG_EVENT = "done_order_pg"
    const val PRODUCT_TITLE_PROPERTY = "product_title"
    const val PRODUCT_DISCOUNT_PROPERTY = "product_discount"

    const val DONE_CREATE_ZIP_FILE_EVENT = "done_create_zip_file"

    const val SHOW_ORDER_DONE_EVENT = "show_order_done"


}