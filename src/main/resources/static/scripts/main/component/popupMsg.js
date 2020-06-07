(function (window) {
    var PopupMsg = Base.createClass('main.component.PopupMsg');
    var Popup = Base.getClass('main.component.Popup');
    var Component = Base.getClass('main.component.Component');
    var Util = Base.getClass('main.base.Util');

    Base.mix(PopupMsg, Component, {
        _tpl: [
            '<div class="wrapper-content clearfix">',
            '<div class="input-section">',

            '<div class="form-group"> ',
            '<label >发给:</label>',
            '<div class="js-email " ><input style="width:500px;" type="email" placeholder="姓名"></div>',
            '</div>',

            '<div class="form-group">',
            '<label >内容:</label>',
            '<div >',
            '<textarea class="js-pwd" placeholder="私信内容" style="font-style:italic;width:500px;"></textarea>',
            '</div>',
            '</div>',

            '<div class="form-group">',
            '<div class="col-input-login">',
            '<a class="btn btn-info js-register" style="width: border-box" href="javascript:void(0);">发送</a>',
            '</div>',
            '</div>',
            '</div>',
            '</div>'].join(''),
        listeners: [{
            name: 'render',
            type: 'custom',
            handler: function () {
                var that = this;
                var oEl = that.getEl();
                that.emailIpt = oEl.find('div.js-email');
                that.pwdIpt = oEl.find('.js-pwd');
                that.initCpn();
            }
        }, {
            name: 'click a.js-register',
            handler: function (oEvent) {
                oEvent.preventDefault();
                var that = this;
                // 值检查
                if (!that.checkVal()) {
                    return;
                }
                alert("开始");
                var oData = that.val();
                $.ajax({
                    url: '/msg/addMessage',
                    type: 'post',
                    dataType: 'json',
                    data: {
                        toName: oData.email,
                        content: oData.pwd
                    }
                }).done(function (oResult) {
                    if (oResult.code === 0) {
//                        window.location.reload();
                        that.emit('register');
                    } else if(oResult.code === 999){
                        //待会修改
                        window.location.href = '/reglogin?next=' + window.encodeURIComponent(window.location.href);
                    } else {
                        oResult.msgname && alert(oResult.msgname);
                        oResult.msgpwd && alert(oResult.msgpwd);
                    }
                }).fail(function () {
                    alert('出现错误，请重试');
                });
            }
        }],
        show: fStaticShow
    }, {
        initialize: fInitialize,
        initCpn: fInitCpn,
        val: fVal,
        checkVal: fCheckVal,
        iptSucc: fIptSucc,
        iptError: fIptError,
        iptNone: fIptNone
    });

    function fStaticShow(oConf) {
        var that = this;
        var oLogin = new PopupMsg(oConf);
        var oPopup = new Popup({
            width: 540,
            title: '发送私信',
            content: oLogin.html()
        });
        oLogin._popup = oPopup;
        Component.setEvents();
    }

    function fInitialize(oConf) {
        var that = this;
        delete oConf.renderTo;
        PopupMsg.superClass.initialize.apply(that, arguments);
    }

    function fInitCpn() {
        var that = this;
        that.emailIpt.find('input').on('focus', Base.bind(that.iptNone, that, that.emailIpt));
        that.pwdIpt.find('input').on('focus', Base.bind(that.iptNone, that, that.pwdIpt));
    }

    function fVal(oData) {
        var that = this;
        var oEl = that.getEl();
        var oEmailIpt = that.emailIpt.find('input');
        var oPwdIpt = that.pwdIpt.find('input');
        var oRemberChk = oEl.find('.js-rember');
        if (arguments.length === 0) {
            return {
                email: $.trim(oEmailIpt.val()),
                pwd: $.trim(oPwdIpt.val()),
                rember: oRemberChk.prop('checked')
            };
        } else {
            oEmailIpt.val($.trim(oData.email));
            oPwdIpt.val($.trim(oData.pwd));
            oRemberChk.prop('checked', !!oData.rember);
        }
    }

    function fCheckVal() {
        var that = this;
        var oData = that.val();
        var bRight = true;
        /*
        if (!Util.isEmail(oData.email)) {
            that.iptError(that.emailIpt, '请填写正确的邮箱');
            bRight = false;
        }*/
        alert("1111");
        if (!oData.email) {
            alert('请填写姓名');
            bRight = false;
        }else if (!oData.pwd) {
            alert('请填写私信内容');
            bRight = false;
        }
        return bRight;
    }

    function fIptSucc(oIpt) {
        var that = this;
        oIpt = $(oIpt);
        that.iptNone(oIpt);
        oIpt.addClass('success');
        if (!oIpt.find('.icon-ok-sign').get(0)) {
            oIpt.append('<i class="input-icon icon-ok-sign"></i>');
        }
    }

    function fIptError(oIpt, sMsg) {
        var that = this;
        oIpt = $(oIpt);
        that.iptNone(oIpt);
        oIpt.addClass('error');
        if (!oIpt.find('.icon-remove-sign').get(0)) {
            oIpt.append('<i class="input-icon icon-remove-sign"></i>');
        }
        var oSpan = oIpt.find('.input-tip');
        if (!oSpan.get(0)) {
            oSpan = $('<span class="input-tip"></span>');
            oIpt.append(oSpan);
        }
        oSpan.html($.trim(sMsg));
    }

    function fIptNone(oIpt) {
        var that = this;
        $(oIpt).removeClass('error success');
    }
})(window);