package com.tieto.ecm.alfresco.monitor.service;

import static org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter.INSTANCE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

import com.tieto.ecm.alfresco.monitor.service.pojo.JobHistory;
import com.tieto.ecm.alfresco.monitor.storage.MonitorStorage;
import com.tieto.ecm.alfresco.monitor.storage.model.MonitorModel;

public class MonitorSearchServiceImpl implements MonitorSearchService {

	private NodeService nodeService;
	private MonitorStorage monitorStorage;	
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setMonitorStorage(MonitorStorage monitorStorage) {
		this.monitorStorage = monitorStorage;
	}
	
	@Override
	public List<JobHistory> getJobsHistory(final int count,final String operation,final String status) {
		final List<JobHistory> result = new ArrayList<>(count);

		NodeRef node = monitorStorage.getOrCreateMonitorJobsContainer();
		final List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(node);
		Collections.reverse(childAssocs);

		Stream<ChildAssociationRef> streamChildrenAssocs = childAssocs.stream();
		
		if (operation != null && !operation.isEmpty()) {
			streamChildrenAssocs = streamChildrenAssocs.filter(ch -> ((String) nodeService.getProperty(ch.getChildRef(), MonitorModel.PROP_OPERATION)).equalsIgnoreCase(operation));
		}
		if (status != null && !status.isEmpty()) {
			streamChildrenAssocs = streamChildrenAssocs.filter(ch -> ((String) nodeService.getProperty(ch.getChildRef(), MonitorModel.PROP_STATUS)).equalsIgnoreCase(status));
		}

		final List<ChildAssociationRef> childAssocsLimit = streamChildrenAssocs.limit(count).collect(Collectors.toList());

		for (ChildAssociationRef child : childAssocsLimit) {
			final String nodeStatus = INSTANCE.convert(String.class, nodeService.getProperty(child.getChildRef(), MonitorModel.PROP_STATUS));
			final NodeRef nodeRef = child.getChildRef();
			final Date date = INSTANCE.convert(Date.class, nodeService.getProperty(child.getChildRef(), ContentModel.PROP_CREATED));
			result.add(new JobHistory(operation, nodeRef, nodeStatus, date));
		}
		return result;
	}
}