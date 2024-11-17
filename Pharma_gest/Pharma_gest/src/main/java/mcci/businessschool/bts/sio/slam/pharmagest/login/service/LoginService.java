package mcci.businessschool.bts.sio.slam.pharmagest.login.service;

import mcci.businessschool.bts.sio.slam.pharmagest.login.dao.LoginDao;

public class LoginService {
    private LoginDao loginDao;

    public LoginService() throws Exception {
        this.loginDao = new LoginDao();
    }

    public boolean seConnecter(String indentifiant, String motdepasse) {
        return loginDao.seConnecter(indentifiant, motdepasse);
    }
}
