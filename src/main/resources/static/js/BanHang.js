const DEFAULT_FORM_DATA = {
  invoiceId: null,
  totalInvoice: 0,
  totalItem: 0,
  note: null,
  type: null,
  customer: null,
  paymentMethod: "Thanh toán tại cửa hàng",
  shippingFee: 0,
};

let formData = { ...DEFAULT_FORM_DATA };

const debounce = (func, delay) => {
  let timer;
  return (...args) => {
    clearTimeout(timer);
    timer = setTimeout(() => func.apply(this, args), delay);
  };
};

const formatPrice = (price) => price.toLocaleString("vi-VN");

const clearData = async () => {
  formData = { ...DEFAULT_FORM_DATA };
  $("#search-product").prop("disabled", true).val("");
  $("#btnPayment, #btnCancel, #input-note").prop("disabled", true);
  $("#input-note").val("");
  $("#count-product, #tongTien, #tong").html("X");
  await loadInvoices();
  $(".product-table tbody").html(`
        <tr>
            <td colspan="7" class="text-center text-secondary">
                <img class="mt-3" src="/icon/empty_box_icon.png" alt="Empty Box Icon">
                <div class="mt-3">Chưa có sản phẩm nào được thêm</div>
            </td>
        </tr>
    `);
};

const enableElement = () => {
  $("#search-product, #input-note, #btnPayment, #btnCancel").prop(
    "disabled",
    false
  );
};

const handlePayment = async () => {
  if (formData.totalItem < 1) {
    alert("Chưa có sản phẩm để thanh toán!");
    return;
  }
  if (formData.type === "Có giao hàng" && formData.customer === null) {
    alert("Bạn hãy thêm thông tin khách hàng để sử dụng dịch vụ giao hàng.");
    return;
  }

  const payload = {
    invoiceId: formData.invoiceId,
    type: formData.type || "Offline",
    ...Object.fromEntries(Object.entries(formData).slice(5)),
  };
  if (!confirm("Xác nhận thanh toán")) return;
  try {
    const response = await fetch(
      "http://localhost:8080/api/v1/ban-hang/payment",
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify(payload),
      }
    );
    const result = await response.json();
    if (result.status === 202) {
      window.location.reload();
    }else{
      alert(result.message);
    }
  } catch (error) {
    console.error("Payment error:", error);
  }
};

const createInvoice = async () => {
  if (formData.totalInvoice > 5) {
    alert("Chỉ có thể tạo tối đa 6 hóa đơn!");
    return;
  }
  try {
    const response = await fetch(
      "http://localhost:8080/api/v1/ban-hang/hoa-don",
      {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({}),
      }
    );
    const result = await response.json();
    if (result.status === 200) window.location.reload();
  } catch (error) {
    console.error("Error creating invoice:", error);
  }
};

const loadInvoices = async () => {
  try {
    const response = await fetch(
      "http://localhost:8080/api/v1/ban-hang/hoa-don",
      {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      }
    );
    const { data: invoices } = await response.json();
    formData.totalInvoice = invoices.length;
    const $invoiceContainer = $("#invoices");
    if (!invoices.length) {
      $("#div-invoice").addClass("text-center mb-3").removeClass("mb-3");
      $invoiceContainer.html(`
                <div class="text-center text-secondary p-3">
                    <img style="width: 50px" class="mt-3" src="/icon/invoice_icon.png" alt="Invoice Icon">
                    <div class="mt-3">Không tìm thấy hóa đơn nào</div>
                </div>
            `);
      return;
    }
    $("#div-invoice").removeClass("text-center mb-3").addClass("mb-3");
    $invoiceContainer.html(
      invoices
        .map(
          (invoice) => `
                        <label class="form-selectgroup-item">
                            <input type="radio" name="name" value="${invoice.id}" class="form-selectgroup-input radio-invoice">
                            <span class="form-selectgroup-label">${invoice.maHoaDon} - ${invoice.tranThai}</span>
                        </label>
                    `
        )
        .join("")
    );
    $(".radio-invoice").on("change", function () {
      handleInvoiceChange(this.value);
    });
  } catch (error) {
    console.error("Error loading invoices:", error);
  }
};

const handleInvoiceChange = async (id) => {
  formData.invoiceId = id;
  enableElement();
  $("#search-customer").prop("disabled", false);
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/ban-hang/hoa-don/${id}`,
      {
        method: "GET",
        headers: { "Content-Type": "application/json" },
      }
    );
    const { data: invoice } = await response.json();

    const ship = invoice.shippingFee < 1 ? formData.shippingFee : invoice.shippingFee;

    const total = parseFloat(invoice.tongTien) + parseFloat(ship);

    formData.customer = invoice.khachHang;
    $("#input-note").val(invoice.ghiChu);
    $("#count-product").html(invoice.listSanPham.length);
    $("#tongTien").html(`${invoice.tongTien.toLocaleString("vi-VN")} đ`);
    $("#tong").html(`${total.toLocaleString("vi-VN")} đ`);
    $("#shipping-fee-info").text(ship.toLocaleString("vi-VN") + " đ");
    formData.totalMoney = invoice.tongTien;
    const $productTableBody = $(".product-table tbody");
    formData.totalItem = invoice.listSanPham.length;

    if (invoice.khachHang && invoice.khachHang.id) {
      const customer = invoice.khachHang;
      const customerId = String(customer.id);
      formData.customerId = customerId;
      const $customerInfo = $(".customer-info");
      const hasCustomerInfo = true;

      // Hiển thị thông tin khách hàng và địa chỉ
      const addressDisplay =
        customer.diaChi && customer.diaChi !== "null, null, null, null"
          ? `<div class="customer-address">${customer.diaChi}</div>`
          : formData.province &&
            formData.district &&
            formData.ward &&
            formData.addressDetail
          ? `<div class="customer-address">${formData.addressDetail}, ${formData.ward}, ${formData.district}, ${formData.province}</div>`
          : '<div class="customer-address">Chưa có địa chỉ</div>';

      $customerInfo
        .html(
          `
                    <div class="customer-name">
                        ${customer.tenKhachHang} - ${customer.soDienThoai} 
                        <span class="remove-customer-btn" style="cursor: pointer; color: #dc3545; margin-left: 5px;" data-customer-id="${customerId}">✕</span>
                    </div>
                    ${addressDisplay}
                    <div class="add-address-btn" data-customer-id="${customerId}">Thay đổi địa chỉ</div>
                    <div class="change-phone-btn" data-customer-id="${customerId}" style="display: inline-block; color: #dc3545 cursor: pointer;">Thay đổi số điện thoại</div>
                `
        )
        .removeClass("text-center text-start")
        .addClass(hasCustomerInfo ? "text-start" : "text-center");

      $(document).on("click", ".change-phone-btn", function () {
        const customerId = $(this).data("customer-id");
        formData.currentCustomerId = customerId;
        $("#changePhoneModal").modal("show");
      });

      $("#changePhoneModal").on("show.bs.modal", function () {
        $("#new-phone-number").val("");
        $("#changePhoneModal .text-danger").text("");
      });

      $("#savePhoneBtn").on("click", async function () {
        $("#changePhoneModal .text-danger").text("");

        const newPhoneNumber = $("#new-phone-number").val().trim();
        let isValid = true;

        // Validate số điện thoại
        if (!newPhoneNumber) {
          $("#new-phone-number")
            .siblings(".text-danger")
            .text("Số điện thoại không được để trống!");
          isValid = false;
        } else {
          const phonePattern = /^[0-9]{10,11}$/;
          if (!phonePattern.test(newPhoneNumber)) {
            $("#new-phone-number")
              .siblings(".text-danger")
              .text("Số điện thoại không hợp lệ! Vui lòng nhập 10-11 chữ số.");
            isValid = false;
          }
        }

        if (!isValid) return;

        // Gọi API để cập nhật số điện thoại
        try {
          const response = await fetch(
            `http://localhost:8080/api/v1/ban-hang/update-phone/${formData.currentCustomerId}/${newPhoneNumber}/${formData.invoiceId}`,
            {
              method: "PUT",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({}),
            }
          );

          const result = await response.json();
          if (result.status === 202) {
            // Đóng modal và hiển thị thông báo
            $("#changePhoneModal").modal("hide");
            alert("Cập nhật số điện thoại thành công!");

            // Cập nhật lại giao diện khách hàng
            await handleInvoiceChange(formData.invoiceId);
          } else {
            alert("Cập nhật số điện thoại thất bại: " + result.message);
          }
        } catch (error) {
          console.error("Error updating phone number:", error);
          alert("Đã có lỗi xảy ra khi cập nhật số điện thoại!");
        }
      });

      $(document)
        .off("click", ".remove-customer-btn")
        .on("click", ".remove-customer-btn", async function () {
          const customerId = $(this).data("customer-id");
          const invoiceId = formData.invoiceId;
          if (
            !confirm("Bạn có chắc chắn muốn xóa khách hàng khỏi hóa đơn này?")
          ) {
            return;
          }
          try {
            const response = await $.ajax({
              url: `http://localhost:8080/api/v1/ban-hang/hoa-don/${invoiceId}/remove-customer`,
              type: "DELETE",
              contentType: "application/json",
              dataType: "json",
            });
            if (response.status === 204) {
              const $customerInfo = $(".customer-info");
              $customerInfo
                .html(
                  `
                                <img style="width: 50px" class="mt-3" src="/icon/id_card_icon.png" alt="Id Card Icon">
                                <div class="mt-3">Chưa có thông tin khách hàng</div>
                            `
                )
                .removeClass("text-center text-start")
                .addClass("text-center");
              formData.customer = null;
              formData.customerId = null;
              formData.currentCustomerId = null;
              formData.province = null;
              formData.district = null;
              formData.ward = null;
              formData.addressDetail = null;
            } else {
              alert("Xóa khách hàng thất bại!");
            }
          } catch (error) {
            console.error("Error removing customer:", error);
            alert("Đã có lỗi xảy ra khi xóa khách hàng!");
          }
        });
    } else {
      const $customerInfo = $(".customer-info");
      const hasCustomerInfo = false;
      $customerInfo
        .html(
          `
                    <img style="width: 50px" class="mt-3" src="/icon/id_card_icon.png" alt="Id Card Icon">
                    <div class="mt-3">Chưa có thông tin khách hàng</div>
                `
        )
        .removeClass("text-center text-start")
        .addClass(hasCustomerInfo ? "text-start" : "text-center");
      formData.customerId = null;
      formData.currentCustomerId = null;
      formData.province = null;
      formData.district = null;
      formData.ward = null;
      formData.addressDetail = null;
    }

    if (!invoice.listSanPham.length) {
      $productTableBody.html(`
                <tr>
                    <td colspan="7" class="text-center">Chưa có sản phẩm nào được thêm</td>
                </tr>
            `);
      return;
    }

    $productTableBody.html(
      invoice.listSanPham
        .map(
          (sanPham, index) => `
                        <tr>
                            <td>${index + 1}</td>
                            <td><img src="${
                              sanPham.hinhAnh
                            }" alt="Product Image" width="50"></td>
                            <td>${sanPham.tenSanPham}<br><small>${
            sanPham.maSanPham
          }</small></td>
                            <td>
                                <input 
                                    type="number" 
                                    min="1"
                                    max="${
                                      sanPham.soLuongTonKho + sanPham.soLuong
                                    }" 
                                    value="${sanPham.soLuong}" 
                                    class="quantity-input form-control"
                                    data-product-id="${sanPham.id}"
                                />
                            </td>
                            <td class="text-right">${formatPrice(
                              sanPham.gia
                            )}</td>
                            <td class="text-right">${formatPrice(
                              sanPham.soLuong * sanPham.gia
                            )}</td>
                            <td class="text-center">
                                <img src="/icon/delete_icon.png" class="remove-btn" data-id="${
                                  sanPham.id
                                }"/>
                            </td>
                        </tr>
                    `
        )
        .join("")
    );

    $(".quantity-input").on("input", function () {
      const $input = $(this);
      const maxValue = Number($input.attr("max"));
      let newQuantity = Number($input.val());
      if (newQuantity < 1) newQuantity = 1;
      if (newQuantity > maxValue) newQuantity = maxValue;
      $input.val(newQuantity);
      debounce(handleQuantityChange, 2000)(
        $input.data("product-id"),
        newQuantity
      );
    });

    $(".remove-btn").on("click", function () {
      removeProduct($(this).data("id"));
    });
  } catch (error) {
    console.error("Error loading invoice:", error);
  }
};

const handleQuantityChange = async (invoiceDetailId, quantity) => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/ban-hang/update-quantity/${invoiceDetailId}/${quantity}`,
      {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
      }
    );
    const data = await response.json();
    if (data.status === 202) await handleInvoiceChange(formData.invoiceId);
  } catch (error) {
    console.error("Quantity update error:", error);
  }
};

const removeProduct = async (itemId) => {
  if (!confirm("Xác nhận xóa?")) return;
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/ban-hang/delete-item/${itemId}`,
      {
        method: "DELETE",
        headers: { "Content-Type": "application/json" },
      }
    );
    const res = await response.json();
    if (res.status === 202) await handleInvoiceChange(formData.invoiceId);
  } catch (error) {
    console.error("Error removing product:", error);
  }
};

// Hàm load danh sách tỉnh/thành
const loadProvinces = async (
  provinceSelect,
  districtSelect,
  wardSelect,
  formDataKey
) => {
  try {
    const response = await fetch("https://provinces.open-api.vn/api/?depth=3", {
      method: "GET",
      headers: {
        Accept: "application/json",
      },
    });

    if (!response.ok) {
      throw new Error("Không thể tải danh sách tỉnh/thành phố");
    }

    const data = await response.json();
    provinceSelect.html(
      '<option value="">Chọn tỉnh/thành</option>' +
        data.map((p) => `<option value="${p.code}">${p.name}</option>`).join("")
    );
    localStorage.setItem("provinceData", JSON.stringify(data));

    provinceSelect.on("change", function () {
      const code = this.value;
      districtSelect
        .html('<option value="">Chọn quận/huyện</option>')
        .prop("disabled", true);
      wardSelect
        .html('<option value="">Chọn xã/phường</option>')
        .prop("disabled", true);

      if (code) {
        const provinces = JSON.parse(localStorage.getItem("provinceData"));
        const province = provinces.find((p) => p.code == code);
        formData[formDataKey + "province"] = province.name;

        districtSelect
          .html(
            '<option value="">Chọn quận/huyện</option>' +
              province.districts
                .map((d) => `<option value="${d.code}">${d.name}</option>`)
                .join("")
          )
          .prop("disabled", false);
      }
    });

    districtSelect.on("change", function () {
      const code = this.value;
      wardSelect
        .html('<option value="">Chọn xã/phường</option>')
        .prop("disabled", true);

      if (code) {
        const provinces = JSON.parse(localStorage.getItem("provinceData"));
        const district = provinces
          .flatMap((p) => p.districts)
          .find((d) => d.code == code);
        formData[formDataKey + "district"] = district.name;

        wardSelect
          .html(
            '<option value="">Chọn xã/phường</option>' +
              district.wards
                .map((w) => `<option value="${w.name}">${w.name}</option>`)
                .join("")
          )
          .prop("disabled", false);
      }
    });

    wardSelect.on("change", function (e) {
      formData[formDataKey + "ward"] = e.target.value;
    });
  } catch (error) {
    console.error("Error loading provinces:", error);
    provinceSelect.html('<option value="">Không thể tải tỉnh/thành</option>');
    alert(
      "Không thể tải danh sách tỉnh/thành phố. Vui lòng kiểm tra kết nối mạng hoặc thử lại sau!"
    );
  }
};

$(document).ready(() => {
  const loadProductSuggestions = async (query = "") => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ban-hang/san-pham?keyword=${encodeURIComponent(
          query
        )}`
      );
      const { data } = await response.json();
      if (data?.length) {
        $("#suggestions")
          .html(
            data
              .map(
                (product) => `
                                    <div class="suggestion-item" data-id="${
                                      product.id
                                    }">
                                        <img src="${
                                          product.hinhAnh ||
                                          "https://via.placeholder.com/50"
                                        }" alt="${product.tenSanPham}">
                                        <div class="product-info">
                                            <div>${product.tenSanPham}</div>
                                            <small>${product.maSanPham}</small>
                                        </div>
                                        <div class="quantity">Số lượng: ${
                                          product.soLuong
                                        }</div>
                                        <div class="price">Giá bán: ${formatPrice(
                                          product.gia
                                        )} VNĐ</div>
                                    </div>
                                `
              )
              .join("")
          )
          .show();
      } else {
        $("#suggestions")
          .html(
            `
                        <div class="suggestion-item text-center text-secondary">
                            Không tìm thấy sản phẩm
                        </div>
                    `
          )
          .show();
      }
    } catch (error) {
      console.error("Search error:", error);
      $("#suggestions").empty().hide();
    }
  };

  $("#search-product")
    .on("focus", function () {
      const query = $(this).val().trim().toLowerCase();
      loadProductSuggestions(query);
    })
    .on(
      "input",
      debounce(async () => {
        const query = $("#search-product").val().trim().toLowerCase();
        loadProductSuggestions(query);
      }, 500)
    );

  $(document).on("click", ".suggestion-item", async function () {
    const productId = $(this).data("id");
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ban-hang/add-to-cart/${productId}/${formData.invoiceId}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({}),
        }
      );
      const result = await response.json();
      if (result.status === 202) await handleInvoiceChange(formData.invoiceId);
    } catch (error) {
      console.error("Add to cart error:", error);
    }
    $("#suggestions").empty().hide();
  });

  $(document).on("click", (e) => {
    if (!$(e.target).closest("#search-product, #suggestions").length) {
      $("#suggestions").empty().hide();
    }
    if (
      !$(e.target).closest("#search-customer, #customer-suggestions").length
    ) {
      $("#customer-suggestions").empty().hide();
    }
  });

  $("#input-note").on(
    "input",
    debounce(async (e) => {
      await fetch(
        `http://localhost:8080/api/v1/ban-hang/update-note/${formData.invoiceId}`,
        {
          method: "PATCH",
          headers: { "Content-Type": "application/json" },
          body: e.target.value.trim() || " ",
        }
      );
    }, 2000)
  );

  $(".radio-order-type").on("change", function () {
    formData.type = this.value;
    $("#div-address").toggle(this.value !== "Offline");
  });

  $("#createInvoiceBtn").on("click", createInvoice);

  $("#btnCancel").on("click", async () => {
    if (!confirm("Xác nhận hủy hóa đơn?")) return;
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ban-hang/cancel-invoice/${formData.invoiceId}`,
        {
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
        }
      );
      if (response.status === 200) {
        await clearData();
        window.location.reload();
      }
    } catch (error) {
      console.error("Cancel invoice error:", error);
    }
  });

  // Load tỉnh/thành cho #deliveryInfoModal
  const $province = $("#province");
  const $district = $("#district");
  const $ward = $("#ward");
  loadProvinces($province, $district, $ward, "");

  $("#address-detail").on(
    "input",
    (e) => (formData.addressDetail = e.target.value)
  );
  $("#recipient_name").on(
    "input",
    (e) => (formData.recipient_name = e.target.value)
  );
  $("#phone_number").on(
    "input",
    (e) => (formData.phone_number = e.target.value)
  );
  $("#email").on("input", (e) => (formData.email = e.target.value));
  $('[name="paymentMethod"]').on("change", function () {
    formData.paymentMethod = this.value;
  });

  const loadCustomerSuggestions = async (query = "") => {
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ban-hang/khach-hang?keyword=${encodeURIComponent(
          query
        )}`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      const { data: customers } = await response.json();
      if (query === "" || !customers?.length) {
        $("#customer-suggestions")
          .html(
            `
                        <div class="customer-suggestion-item add-new-customer" style="font-weight: bold; color: #007bff;">
                            <div class="customer-info">
                                <div>Thêm khách hàng mới</div>
                            </div>
                        </div>
                    `
          )
          .show();
      } else {
        $("#customer-suggestions")
          .html(
            `
                        <div class="customer-suggestion-item add-new-customer" style="font-weight: bold; color: #007bff;">
                            <div class="customer-info">
                                <div>Thêm khách hàng mới</div>
                            </div>
                        </div>
                        ${customers
                          .map(
                            (customer) => `
                                    <div class="customer-suggestion-item" data-id="${customer.id}">
                                        <div class="customer-info">
                                            <div>${customer.tenKhachHang}</div>
                                            <small>${customer.maKhachHang} - ${customer.soDienThoai}</small>
                                        </div>
                                    </div>
                                `
                          )
                          .join("")}
                    `
          )
          .show();
      }
    } catch (error) {
      console.error("Search customer error:", error);
      $("#customer-suggestions").empty().hide();
    }
  };

  $("#search-customer")
    .on("focus", function () {
      const query = $(this).val().trim().toLowerCase();
      loadCustomerSuggestions(query);
    })
    .on(
      "input",
      debounce(async () => {
        const query = $("#search-customer").val().trim().toLowerCase();
        loadCustomerSuggestions(query);
      }, 500)
    );

  $(document).on("click", ".customer-suggestion-item", async function () {
    const customerId = String($(this).data("id"));
    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ban-hang/add-customer/${formData.invoiceId}/${customerId}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
        }
      );
      if (response.status === 200)
        await handleInvoiceChange(formData.invoiceId);
    } catch (error) {
      console.error("Search customer error:", error);
    } finally {
      $("#customer-suggestions").empty().hide();
    }
  });

  $(document).on("click", ".add-address-btn", function () {
    const customerId = $(this).data("customer-id");
    formData.currentCustomerId = customerId;
    $("#addAddressModal").modal("show");
  });

  $(document).on("click", ".add-new-customer", function () {
    $("#deliveryInfoModal").modal("show");
  });

  $("#saveDeliveryInfoBtn").on("click", async function () {
    $("#deliveryInfoModal .text-danger").text("");

    const recipientName = $("#recipient_name").val().trim();
    const phoneNumber = $("#phone_number").val().trim();
    const email = $("#email").val().trim();
    const province = $("#province option:selected").text();
    const district = $("#district option:selected").text();
    const ward = $("#ward option:selected").text();
    const addressDetail = $("#address-detail").val().trim();

    let isValid = true;

    if (!recipientName) {
      $("#recipient_name")
        .siblings(".text-danger")
        .text("Tên người nhận không được để trống!");
      isValid = false;
    }

    if (!phoneNumber) {
      $("#phone_number")
        .siblings(".text-danger")
        .text("Số điện thoại không được để trống!");
      isValid = false;
    } else {
      const phonePattern = /^[0-9]{10,11}$/;
      if (!phonePattern.test(phoneNumber)) {
        $("#phone_number")
          .siblings(".text-danger")
          .text("Số điện thoại không hợp lệ! Vui lòng nhập 10-11 chữ số.");
        isValid = false;
      }
    }

    if (email && !/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      $("#email").siblings(".text-danger").text("Email không hợp lệ!");
      isValid = false;
    }

    if (!province || province === "Chọn tỉnh/thành") {
      $("#province")
        .siblings(".text-danger")
        .text("Vui lòng chọn tỉnh/thành phố!");
      isValid = false;
    }

    if (!district || district === "Chọn quận/huyện") {
      $("#district").siblings(".text-danger").text("Vui lòng chọn quận/huyện!");
      isValid = false;
    }

    if (!ward || ward === "Chọn xã/phường") {
      $("#ward").siblings(".text-danger").text("Vui lòng chọn xã/phường!");
      isValid = false;
    }

    if (!addressDetail) {
      $("#address-detail")
        .siblings(".text-danger")
        .text("Địa chỉ cụ thể không được để trống!");
      isValid = false;
    }

    if (!isValid) return;

    const deliveryInfo = {
      recipient_name: recipientName,
      phone_number: phoneNumber,
      email: email || null,
      province: province,
      district: district,
      ward: ward,
      addressDetail: addressDetail,
    };

    if (!email) {
      const confirmSave = confirm(
        "Sẽ không thể tạo mật khẩu nếu email để trống. Bạn có muốn tiếp tục lưu thông tin không?"
      );
      if (!confirmSave) return;
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ban-hang/create-customer/${formData.invoiceId}`,
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify(deliveryInfo),
        }
      );

      const result = await response.json();

      if (result.status === 200) {
        $("#recipient_name").val("");
        $("#phone_number").val("");
        $("#email").val("");
        $("#province").val("");
        $("#district").val("").prop("disabled", true);
        $("#ward").val("").prop("disabled", true);
        $("#address-detail").val("");
        $("#deliveryInfoModal").modal("hide");
        alert("Tạo khách hàng thành công!");
        await handleInvoiceChange(formData.invoiceId);
      } else {
        alert("Tạo khách hàng thất bại: " + result.message);
      }
    } catch (error) {
      alert(error);
    }
  });

  // Change Address
  $("#addAddressModal").on("show.bs.modal", async function () {
    $("#existing-addresses").val("");
    $("#modal-province").val("");
    $("#modal-district").val("").prop("disabled", true);
    $("#modal-ward").val("").prop("disabled", true);
    $("#modal-address-detail").val("");
    $("#addAddressModal .text-danger").text("");

    $("#existingAddress").prop("checked", true);
    $("#existing-address-section").show();
    $("#new-address-section").hide();

    const $modalProvince = $("#modal-province");
    const $modalDistrict = $("#modal-district");
    const $modalWard = $("#modal-ward");
    loadProvinces($modalProvince, $modalDistrict, $modalWard, "modal_");

    $("#modal-address-detail").on(
      "input",
      (e) => (formData.modal_addressDetail = e.target.value)
    );

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ban-hang/customer-addresses/${formData.currentCustomerId}`,
        {
          method: "GET",
          headers: { "Content-Type": "application/json" },
        }
      );
      const result = await response.json();
      if (result.status === 200 && result.data?.length) {
        const addresses = result.data;
        $("#existing-addresses").html(
          '<option value="">Chọn địa chỉ</option>' +
            addresses
              .map((address, index) => {
                const fullAddress = `${address.addressDetail}, ${address.ward}, ${address.district}, ${address.province}`;
                return `<option value="${index}" data-province="${address.province}" data-district="${address.district}" data-ward="${address.ward}" data-address-detail="${address.addressDetail}">${fullAddress}</option>`;
              })
              .join("")
        );
      } else {
        $("#existing-addresses").html(
          '<option value="">Không có địa chỉ nào</option>'
        );
      }
    } catch (error) {
      console.error("Error loading addresses:", error);
      $("#existing-addresses").html(
        '<option value="">Không có địa chỉ nào</option>'
      );
    }
  });

  $(".address-option").on("change", function () {
    const option = $(this).val();
    if (option === "existing") {
      $("#existing-address-section").show();
      $("#new-address-section").hide();
    } else {
      $("#existing-address-section").hide();
      $("#new-address-section").show();
    }
  });

  $("#saveAddressBtn").on("click", async function () {
    $("#addAddressModal .text-danger").text("");
    const option = $(".address-option:checked").val();
    let addressData = {};

    if (option === "existing") {
      const selectedAddress = $("#existing-addresses option:selected");
      const addressIndex = selectedAddress.val();
      if (!addressIndex) {
        alert("Vui lòng chọn một địa chỉ!");
        return;
      }

      addressData = {
        province: selectedAddress.data("province"),
        district: selectedAddress.data("district"),
        ward: selectedAddress.data("ward"),
        addressDetail: selectedAddress.data("address-detail"),
      };
    } else {
      const province = $("#modal-province option:selected").text();
      const district = $("#modal-district option:selected").text();
      const ward = $("#modal-ward option:selected").text();
      const addressDetail = $("#modal-address-detail").val().trim();

      let isValid = true;

      if (!province || province === "Chọn tỉnh/thành") {
        $("#modal-province")
          .siblings(".text-danger")
          .text("Vui lòng chọn tỉnh/thành phố!");
        isValid = false;
      }

      if (!district || district === "Chọn quận/huyện") {
        $("#modal-district")
          .siblings(".text-danger")
          .text("Vui lòng chọn quận/huyện!");
        isValid = false;
      }

      if (!ward || ward === "Chọn xã/phường") {
        $("#modal-ward")
          .siblings(".text-danger")
          .text("Vui lòng chọn xã/phường!");
        isValid = false;
      }

      if (!addressDetail) {
        $("#modal-address-detail")
          .siblings(".text-danger")
          .text("Địa chỉ cụ thể không được để trống!");
        isValid = false;
      }

      if (!isValid) return;

      addressData = {
        province: province,
        district: district,
        ward: ward,
        addressDetail: addressDetail,
      };
    }

    try {
      const response = await fetch(
        `http://localhost:8080/api/v1/ban-hang/update-address/${formData.currentCustomerId}/${formData.invoiceId}`,
        {
          method: "PUT",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(addressData),
        }
      );

      const result = await response.json();
      if (result.status === 202) {
        formData.province = addressData.province;
        formData.district = addressData.district;
        formData.ward = addressData.ward;
        formData.addressDetail = addressData.addressDetail;

        $("#existing-addresses").val("");
        $("#modal-province").val("");
        $("#modal-district").val("").prop("disabled", true);
        $("#modal-ward").val("").prop("disabled", true);
        $("#modal-address-detail").val("");
        $("#addAddressModal .text-danger").text("");

        $("#addAddressModal").modal("hide");
        alert("Cập nhật địa chỉ thành công!");

        await handleInvoiceChange(formData.invoiceId);
      } else {
        alert("Cập nhật địa chỉ thất bại: " + result.message);
      }
    } catch (error) {
      console.error("Error updating address:", error);
      alert("Đã có lỗi xảy ra khi cập nhật địa chỉ!");
    }
  });

  $(".radio-order-type").on("change", function () {
    formData.type = this.value;
    const $paymentButton = $("#btnPayment");

    if (this.value === "Có giao hàng") {
      $("#shipping-fee-container").show();
      formData.shippingFee = $("#shipping-fee").val();
    } else {
      $("#shipping-fee-container").hide();
      $("#shipping-fee").val(0);
      $("#shipping-fee-error").text("");
      $("#shipping-fee-info").text(0 + " đ");
      $("#tong").text(formData.totalMoney.toLocaleString("vi-VN") + " đ");
      formData.shippingFee = 0;
      $paymentButton.prop("disabled", false);
    }
  });

  const updatePaymentInfo = () => {
    const shippingFee = parseFloat($("#shipping-fee").val()) || 0;
    const totalAmount = formData.totalMoney + shippingFee;

    $("#tong").text(totalAmount.toLocaleString("vi-VN") + " đ");
    $("#shipping-fee-info").text(shippingFee.toLocaleString("vi-VN") + " đ");
  };

  $("#shipping-fee").on("change", function () {
    const shippingFee = $(this).val();
    const $paymentButton = $("#btnPayment");

    if (shippingFee === "") {
      formData.shippingFee = 0;
      $(this).val(0);
      $paymentButton.prop("disabled", false);
    } else if (shippingFee < 0) {
      $("#shipping-fee-error").text("Phí giao hàng không được âm");
      formData.shippingFee = 0;
      $paymentButton.prop("disabled", true);
    } else if (shippingFee > 10000000) {
      $("#shipping-fee-error").text(
        "Phí giao hàng không được lớn hơn 10 triệu"
      );
      formData.shippingFee = 0;
      $paymentButton.prop("disabled", true);
    } else {
      $("#shipping-fee-error").text("");
      formData.shippingFee = shippingFee;
      $paymentButton.prop("disabled", false);
    }

    updatePaymentInfo();
  });
  loadInvoices();
});
