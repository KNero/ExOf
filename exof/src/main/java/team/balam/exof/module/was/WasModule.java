package team.balam.exof.module.was;

import team.balam.exof.db.ListenerDao;
import team.balam.exof.environment.vo.PortInfo;
import team.balam.exof.module.Module;

import java.util.ArrayList;
import java.util.List;

public class WasModule implements Module {
    private List<JettyModule> jettyModuleList;

    @Override
    public void start() throws Exception {
        List<PortInfo> portInfoList = ListenerDao.selectJettyModule();
        if (!portInfoList.isEmpty()) {
            jettyModuleList = new ArrayList<>(portInfoList.size());

            for (PortInfo portInfo : portInfoList) {
                JettyModule jettyModule = new JettyModule();
                jettyModule.setPortInfo(portInfo);
                jettyModule.start();

                jettyModuleList.add(jettyModule);
            }
        }
    }

    @Override
    public void stop() throws Exception {
        if (jettyModuleList != null) {
            for (JettyModule jettyModule : jettyModuleList) {
                jettyModule.stop();
            }
        }
    }
}
