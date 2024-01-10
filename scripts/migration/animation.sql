/*
Animations (MCPathID = 12)
available on DVD disks (d.StorageID IS NOT NULL OR d.StoragePlace IS NOT NULL)
to be migrated
*/


SELECT 
  d.Title AS DiskTitle,
  m.Title AS ContentTitle,
  m.FileName  
FROM (((VProduct as p
INNER JOIN VProductMPEG4 pm ON p.VProductID = pm.VProductID)
INNER JOIN MPEG4CDCont m ON pm.MPEG4CDContId = m.MPEG4CDContId)
INNER JOIN Rec as r ON m.RecID = r.RecID)
INNER JOIN Disk AS d ON r.DiskID = d.DiskID 
WHERE p.MCPathID = 12
  AND (d.StorageID IS NOT NULL OR d.StoragePlace IS NOT NULL)
ORDER BY 1, 3, 2